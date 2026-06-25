package main.dao;

import main.entities.Account;
import main.entities.AccountType;
import main.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {
        "JdbcUrl=jdbc:h2:mem:test_database;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "JdbcUsername=user",
        "JdbcPassword=",
        "JdbcDriver=org.h2.Driver"
})
@ActiveProfiles("test")
class AccountDaoTest {
    @Autowired
    AccountDao subj;
    @Autowired
    UserDao userDao;
    User user;

    @BeforeEach
    void setUp() {
    }

    @Test
    void findByIdHappyPath() {
        user = new User();
        user.setEmail("email");
        user.setHashPassword("hash");
        userDao.insert(user);

        Account account = new Account();
        account.setAccountType(AccountType.DEBIT);
        account.setBalance(BigDecimal.valueOf(100));
        account.setName("account");
        account.setUserId(user.getId());

        subj.insert(account);

        Account foundAccount = subj.findById(account.getId());

        assertEquals("account", foundAccount.getName());

    }
}