package main.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import main.entities.Transaction;
import main.exceptions.TransactionNotFound;
import main.repositories.AccountRepository;
import main.repositories.TransactionRepository;
import main.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

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

//    @Transactional
//    public Transaction createRegularTransaction(TransactionType transactionType, long transactionCategoryId, BigDecimal amount, long userId, long accountId) throws AccountNotFoundException {
//        Account account = accountRepository.findByIdAndActiveTrue(accountId).orElseThrow(AccountNotFoundException::new);
//        Category.
//        if(account.getUser().getId() != userId) throw new AccessNotAllowed("nope");
//        if(transactionType == TransactionType.INCOME){
//            return createIncome(transactionCategoryId, amount, accountId);
//        }else{
//            throw new RuntimeException("runtime");
//            //            return createExpense(transactionCategory, amount, accountId);
//        }
//    }

//    public Transaction createIncome(long transactionCategory, BigDecimal amount, long accountId){
//        int res = accountRepository.updateBalanceById(amount,accountId);
//        Account acc = accountRepository.getReferenceById(accountId);
//        if(res == 1){
//            return transactionRepository.save(new Transaction(TransactionType.INCOME, acc, transactionCategory, amount));
//        }else{
//            throw new TransactionNotFound("not found");
//        }
//    }
//
//    public Transaction createExpense(Category transactionCategory, BigDecimal amount, long accountId){
//        //тут нужно проверить баланс, можем ли мы совершить такой расход с аккаунта
//    }
}
