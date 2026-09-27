package main.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import main.entities.Account;
import main.entities.Category;
import main.entities.Transaction;
import main.entities.TransactionType;
import main.exceptions.AccessNotAllowed;
import main.exceptions.BalanceException;
import main.exceptions.TransactionNotFound;
import main.repositories.AccountRepository;
import main.repositories.CategoryRepository;
import main.repositories.TransactionRepository;
import main.repositories.UserRepository;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public List<Transaction> findAllByAccId(long accountId, long userId){
        List<Transaction> list = transactionRepository.findAllByAccountIdAndUserIdWithCategory(accountId, userId);
        if(list.isEmpty()){
            throw new TransactionNotFound("not found");
        }
        return list;
    }

    @Transactional
    public Transaction getTransactionById(long userId, long transactionId){
        return transactionRepository.findByIdAndUserId(transactionId, userId).orElseThrow(AccessNotAllowed::new);
    }

    @Transactional
    public Transaction createRegularTransaction(TransactionType transactionType, long categoryId, BigDecimal amount, long userId, long accountId) throws AccountNotFoundException {
        Account account = accountRepository.findByIdAndActiveTrue(accountId).orElseThrow(AccountNotFoundException::new);
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId).orElseThrow(AccessNotAllowed::new);
        if(category.getUser().getId() != userId) throw new AccessNotAllowed("nope");
        if(account.getUser().getId() != userId) throw new AccessNotAllowed("nope");
        if(transactionType == TransactionType.INCOME){
            return createIncome(category, amount, account);
        }else{
            return createExpense(category, amount, account);
        }
    }

    public Transaction createIncome(Category category, BigDecimal amount, Account account){
        account.setBalance(account.getBalance().add(amount));
        return transactionRepository.save(new Transaction(TransactionType.INCOME, account, category, amount));
    }

    public Transaction createExpense(Category category, BigDecimal amount, Account account){
        if(account.getBalance().compareTo(amount) < 0) throw new BalanceException("balance too low");
        account.setBalance(account.getBalance().subtract(amount));
        return transactionRepository.save(new Transaction(TransactionType.EXPENSE, account, category, amount));
    }
}
