package main.service;

import main.ApplicationContext.ApplicationContext;
import main.dao.AccountDao;
import main.dao.TransactionDao;
import main.dao.UserDao;
import main.entities.Account;
import main.entities.AccountType;
import main.entities.TransactionCategory;
import main.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionServiceTest {
    TransactionService subj;
    AccountDao accountDao;
    UserDao userDao;
    TransactionDao transactionDao;

    @BeforeEach
    void setUp() {
        System.setProperty("JdbcUrl", "jdbc:h2:mem:test_database");
        System.setProperty("JdbcUsername", "user");
        System.setProperty("JdbcPassword", "");
        ApplicationContext context = new ApplicationContext();
        subj = context.getTransactionService();
        accountDao = context.getAccountDao();
        userDao = context.getUserDao();
        transactionDao = context.getTransactionDao();


    }

    @Test
    void createTransactionHappyPath() {
        User user = new User();
        user.setEmail("email");
        user.setHashPassword("hash");
        userDao.insert(user);

        TransactionCategory category = new TransactionCategory();
        category.setId(1);
        category.setTransactionCategoryName("category");
        category.setUserid(user.getId());
        transactionDao.insertCategory(category);

        Account from = new Account();
        from.setAccountType(AccountType.DEBIT);
        from.setBalance(BigDecimal.valueOf(100));
        from.setName("from");
        from.setUserId(user.getId());
        accountDao.insert(from);

        Account to = new Account();
        to.setAccountType(AccountType.DEBIT);
        to.setBalance(BigDecimal.valueOf(100));
        to.setName("to");
        to.setUserId(user.getId());
        accountDao.insert(to);

        subj.createTransaction(from, to, BigDecimal.valueOf(50), 1);
        assertEquals(BigDecimal.valueOf(50), from.getBalance());
        assertEquals(BigDecimal.valueOf(150), to.getBalance());
    }

    @Test
    void createTransactionRollbackWhenRecipientIdNotValid(){
        AccountDao spyAccountDao = Mockito.spy(accountDao);

        User user = new User();
        user.setEmail("email");
        user.setHashPassword("hash");
        userDao.insert(user);

        TransactionCategory category = new TransactionCategory();
        category.setId(1);
        category.setTransactionCategoryName("category");
        category.setUserid(user.getId());
        transactionDao.insertCategory(category);

        Account from = new Account();
        from.setAccountType(AccountType.DEBIT);
        from.setBalance(BigDecimal.valueOf(100));
        from.setName("from");
        from.setUserId(user.getId());
        accountDao.insert(from);

        Account to = new Account();
        to.setAccountType(AccountType.DEBIT);
        to.setBalance(BigDecimal.valueOf(100));
        to.setName("to");
        to.setUserId(user.getId());
        accountDao.insert(to);

        Mockito.doThrow(new RuntimeException("DB dropped")).when(spyAccountDao).update(to);

        subj.createTransaction(from, to, BigDecimal.valueOf(50),2);
        assertEquals(BigDecimal.valueOf(100), from.getBalance());
        assertEquals(BigDecimal.valueOf(100), to.getBalance());
    }
}