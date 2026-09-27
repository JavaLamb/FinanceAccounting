package main.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import main.entities.Account;
import main.entities.Category;
import main.entities.Transaction;
import main.entities.TransactionType;
import main.exceptions.AccessNotAllowed;
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
//    @Transactional
//    public Transaction createTransactionByAccountIdAndUserId(long userId, long accountId) {
//        Account account = accountRepository.getReferenceById(accountId);
//
//    }

    @Transactional
    public Transaction createRegularTransaction(TransactionType transactionType, long categoryId, BigDecimal amount, long userId, long accountId) throws AccountNotFoundException {
        Account account = accountRepository.findByIdAndActiveTrue(accountId).orElseThrow(AccountNotFoundException::new);
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId).orElseThrow(AccessNotAllowed::new);
        if(category.getUser().getId() != userId) throw new AccessNotAllowed("nope");
        if(account.getUser().getId() != userId) throw new AccessNotAllowed("nope");
        if(transactionType == TransactionType.INCOME){
            return createIncome(category, amount, accountId, account);
        }else{
            throw new RuntimeException("runtime");
            //            return createExpense(transactionCategory, amount, accountId);
        }
    }

    public Transaction createIncome(Category category, BigDecimal amount, long accountId, Account account){
        account.setBalance(account.getBalance().add(amount));
        return transactionRepository.save(new Transaction(TransactionType.INCOME, account, category, amount));
    }
//
//    public Transaction createExpense(Category transactionCategory, BigDecimal amount, long accountId){
//        //тут нужно проверить баланс, можем ли мы совершить такой расход с аккаунта
//    }
}
