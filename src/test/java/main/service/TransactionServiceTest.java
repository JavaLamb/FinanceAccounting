package main.service;

import main.dao.AccountDao;
import main.dao.TransactionDao;
import main.dao.UserDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ActiveProfiles("test")
class TransactionServiceTest {
    @Autowired
    TransactionService subj;
    @Autowired
    AccountDao accountDao;
    @Autowired
    UserDao userDao;
    @Autowired
    TransactionDao transactionDao;
    @Autowired
    DataSource dataSource;

    @BeforeEach
    void setUp() {
        try (Connection connection = dataSource.getConnection();
             Statement stmnt = connection.createStatement()) {
            stmnt.execute("set referential_integrity false");

            stmnt.execute("truncate table users restart identity");
            stmnt.execute("truncate table transactions restart identity");
            stmnt.execute("truncate table accounts restart identity");
            stmnt.execute("truncate table transaction_category restart identity");

            stmnt.execute("set referential_integrity true");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void clearDB() {

    }

//    @Test
//    void createTransactionHappyPath() {
//        User user = new User();
//        user.setEmail("email");
//        user.setHashPassword("hash");
//        userDao.insert(user);
//
//        TransactionCategory category = new TransactionCategory();
//        category.setId(1);
//        category.setTransactionName("category");
//        category.setUserid(user.getId());
//        transactionDao.insertCategory(category);
//
//        Account from = new Account();
//        from.setAccountType(AccountType.DEBIT);
//        from.setBalance(BigDecimal.valueOf(100));
//        from.setName("from");
//        from.setUserId(user.getId());
//        accountDao.insert(from);
//
//        Account to = new Account();
//        to.setAccountType(AccountType.DEBIT);
//        to.setBalance(BigDecimal.valueOf(100));
//        to.setName("to");
//        to.setUserId(user.getId());
//        accountDao.insert(to);
//
//        subj.createTransaction(from, to, BigDecimal.valueOf(50), 1);
//        assertEquals(BigDecimal.valueOf(50), from.getBalance());
//        assertEquals(BigDecimal.valueOf(150), to.getBalance());
//    }
//
//    @Test
//    void createTransactionRollbackWhenCategoryIdNotValid() throws Exception {
//        User user = new User();
//        user.setEmail("email");
//        user.setHashPassword("hash");
//        userDao.insert(user);
//
//        TransactionCategory category = new TransactionCategory();
//        category.setId(1);
//        category.setTransactionName("category");
//        category.setUserid(user.getId());
//        transactionDao.insertCategory(category);
//
//        Account from = new Account();
//        from.setAccountType(AccountType.DEBIT);
//        from.setBalance(BigDecimal.valueOf(100));
//        from.setName("from");
//        from.setUserId(user.getId());
//        accountDao.insert(from);
//
//        Account to = new Account();
//        to.setAccountType(AccountType.DEBIT);
//        to.setBalance(BigDecimal.valueOf(100));
//        to.setName("to");
//        to.setUserId(user.getId());
//        accountDao.insert(to);
//
//        try (MockedStatic<ConnectionHolder> holderMock = Mockito.mockStatic(ConnectionHolder.class)) {
//            Connection real = dataSource.getConnection();
//            Connection spy = Mockito.spy(real);
//
//            holderMock.when(ConnectionHolder::get).thenReturn(spy);
//
//            assertThrows(RuntimeException.class, () -> subj.createTransaction(from, to, BigDecimal.valueOf(50), 2));
//
//            Mockito.verify(spy, Mockito.times(1)).rollback();
//        }
//
//        assertEquals(0, BigDecimal.valueOf(100).compareTo(accountDao.findById(from.getId()).getBalance()));
//        assertEquals(0, BigDecimal.valueOf(100).compareTo(accountDao.findById(to.getId()).getBalance()));
//    }
}