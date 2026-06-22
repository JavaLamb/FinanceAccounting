package main.service;

import main.ApplicationContext.ApplicationContext;
import main.dao.AccountDao;
import main.dao.TransactionDao;
import main.dao.Transactional.ConnectionHolder;
import main.dao.UserDao;
import main.entities.Account;
import main.entities.AccountType;
import main.entities.TransactionCategory;
import main.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.sql.Connection;

import static main.dao.DaoFactory.getConnection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void createTransactionRollbackWhenCategoryIdNotValid() throws Exception {
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

        try (MockedStatic<ConnectionHolder> holderMock = Mockito.mockStatic(ConnectionHolder.class)) {
            Connection real = getConnection();
            Connection spy = Mockito.spy(real);

            holderMock.when(ConnectionHolder::get).thenReturn(spy);

            assertThrows(RuntimeException.class, () -> subj.createTransaction(from, to, BigDecimal.valueOf(50), 2));

            Mockito.verify(spy, Mockito.times(1)).rollback();
        }

        assertEquals(0, BigDecimal.valueOf(100).compareTo(accountDao.findById(from.getId()).getBalance()));
        assertEquals(0, BigDecimal.valueOf(100).compareTo(accountDao.findById(to.getId()).getBalance()));
    }
}