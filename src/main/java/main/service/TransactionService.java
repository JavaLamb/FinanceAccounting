package main.service;

import jakarta.validation.constraints.NotNull;
import main.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import main.entities.Account;
import main.entities.Category;
import main.entities.Transaction;
import main.entities.TransactionType;
import main.repositories.AccountRepository;
import main.repositories.CategoryRepository;
import main.repositories.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.*;

@Validated
@RequiredArgsConstructor
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Transaction> findAllByAccId(long accountId, long userId) {
        Account account = accountRepository.findByIdAndActiveTrue(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account",
                        String.valueOf(accountId)
                ));
        if (!account.isOwner(userId)) {
            throw new BusinessLogicException(
                    "You must be the owner of account",
                    HttpStatus.FORBIDDEN,
                    "ACCESS_DENIED"
            );
        }
        return transactionRepository.findAllByAccountIdAndUserIdWithCategory(accountId, userId);
    }

    @Transactional(readOnly = true)
    public Transaction getTransactionById(long userId, long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction",
                        String.valueOf(transactionId)
                ));
        Account fromAccount = transaction.getFromAccount();
        Account toAccount = transaction.getToAccount();
        boolean isFromOwner = fromAccount != null && fromAccount.isOwner(userId);
        boolean isToOwner = toAccount != null && toAccount.isOwner(userId);
        if (!isFromOwner && !isToOwner) {
            throw new BusinessLogicException(
                    "Transaction info is available to the participant only",
                    HttpStatus.FORBIDDEN,
                    "ACCESS_DENIED"
            );
        }
        return transaction;
    }


    @Transactional
    public Transaction createRegularTransaction(TransactionType transactionType, long categoryId, BigDecimal amount, long userId, long accountId) {
        Account account = accountRepository.findByIdAndActiveTrue(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account", String.valueOf(accountId)));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category", String.valueOf(categoryId)));
        if (!category.isUserOwner(userId)) throw new BusinessLogicException(
                "To create a transaction, you must be the category owner",
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED"
        );
        if (!account.isOwner(userId)) throw new BusinessLogicException(
                "To create a transaction, you must be the account owner",
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED"
        );
        if (transactionType == TransactionType.INCOME) {
            return createIncome(category, amount, account);
        } else {
            return createExpense(category, amount, account);
        }
    }


    @Transactional
    public Transaction createTransfer(Long toAccountId, BigDecimal amount, Long categoryId, long fromAccountId, long userId) {
        Account fromAccount = accountRepository.findByIdAndActiveTrue(fromAccountId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account",
                        String.valueOf(fromAccountId)));
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category",
                        String.valueOf(categoryId)));
        if (!fromAccount.isOwner(userId)) throw new BusinessLogicException(
                "The originating account must belong to the transfer originator",
                HttpStatus.FORBIDDEN,
                "ACCESS_DENIED");
        if (!isBalanceValid(fromAccount, amount))
            throw new ValidationException(Map.of("amount",
                    List.of("The amount of transaction exceeds the balance of account")));
        Account toAccount = accountRepository.findByIdAndActiveTrue(toAccountId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account",
                        String.valueOf(toAccountId)
                ));

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        return transactionRepository.save(new Transaction(TransactionType.TRANSFER, fromAccount, toAccount, category, amount));
    }

    @Transactional
    public Transaction changeAmount(long userId, long transactionId, @NotNull BigDecimal newAmount) {
        Transaction oldTransaction = transactionRepository.findByIdWithAccounts(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction",
                        String.valueOf(transactionId)
                ));
        BigDecimal changeAmount = newAmount.subtract(oldTransaction.getAmount());
        Account fromAccount = oldTransaction.getFromAccount();
        Account toAccount = oldTransaction.getToAccount();
        validateTransferOwnership(oldTransaction, userId);

        if (fromAccount != null) {
            if (fromAccount.getBalance().subtract(changeAmount).signum() < 0) {
                throw new ValidationException(
                        Map.of("Amount",
                                List.of("Impossible to change transaction amount: balance of account " + fromAccount.getName() + " will become negative")));
            }
            fromAccount.setBalance(fromAccount.getBalance().subtract(changeAmount));
        }
        if (toAccount != null) {
            if (toAccount.getBalance().add(changeAmount).signum() < 0) {
                throw new ValidationException(
                        Map.of("Amount",
                                List.of("Impossible to change transaction amount: balance of account " + toAccount.getName() + " will become negative")));
            }
            toAccount.setBalance((toAccount.getBalance().add(changeAmount)));
        }
        oldTransaction.setAmount(newAmount);
        return oldTransaction;
    }

    @Transactional
    public void deleteTransaction(long transactionId, long userId) {
        Transaction oldTransaction = transactionRepository.findByIdWithAccounts(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction",
                        String.valueOf(transactionId)));
        BigDecimal amount = oldTransaction.getAmount();
        Account fromAccount = oldTransaction.getFromAccount();
        Account toAccount = oldTransaction.getToAccount();
        validateTransferOwnership(oldTransaction, userId);

        if (fromAccount == null) {
            if (toAccount.getBalance().subtract(amount).signum() < 0) {
                throw new BusinessLogicException(
                        "Impossible to delete transaction: balance of account "
                                + toAccount.getName() + " will become negative",
                        HttpStatus.BAD_REQUEST,
                        "INSUFFICIENT_FUNDS"
                );
            }
            toAccount.setBalance(toAccount.getBalance().subtract(amount));
        } else if (toAccount == null) {
            fromAccount.setBalance(fromAccount.getBalance().add(amount));
        } else {
            if (toAccount.getBalance().subtract(amount).signum() < 0) {
                throw new BusinessLogicException(
                        "Impossible to delete transaction: balance of destination account "
                                + toAccount.getName() + " will become negative",
                        HttpStatus.BAD_REQUEST,
                        "INSUFFICIENT_FUNDS"
                );
            }
            fromAccount.setBalance(fromAccount.getBalance().add(amount));
            toAccount.setBalance(toAccount.getBalance().subtract(amount));
        }
        transactionRepository.delete(oldTransaction);
    }

    private Transaction createIncome(Category category, BigDecimal amount, Account account) {
        account.setBalance(account.getBalance().add(amount));
        return transactionRepository.save(new Transaction(TransactionType.INCOME, account, category, amount));
    }

    private Transaction createExpense(Category category, BigDecimal amount, Account account) {
        if (!isBalanceValid(account, amount))
            throw new ValidationException(
                    Map.of("amount",
                            List.of("The amount of transaction exceeds the balance of account")));
        account.setBalance(account.getBalance().subtract(amount));
        return transactionRepository.save(new Transaction(TransactionType.EXPENSE, account, category, amount));
    }

    private boolean isBalanceValid(Account account, BigDecimal amount) {
        return account.getBalance().compareTo(amount) >= 0;
    }

    private void validateTransferOwnership(Transaction transaction, long userId){
        Account fromAccount = transaction.getFromAccount();
        Account toAccount = transaction.getToAccount();

        if((fromAccount != null && !fromAccount.isOwner(userId))
                || (toAccount != null && !toAccount.isOwner(userId))){
            throw new BusinessLogicException(
                    "You must be the owner of both accounts",
                    HttpStatus.FORBIDDEN,
                    "ACCESS_DENIED"
            );
        }
    }
}