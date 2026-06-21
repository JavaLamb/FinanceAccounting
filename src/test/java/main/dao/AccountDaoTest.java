package main.dao;

import main.ApplicationContext.ApplicationContext;
import main.entities.Account;
import main.entities.AccountType;
import main.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class AccountDaoTest {
    AccountDao subj;
    User user;

    @BeforeEach
    void setUp(){
        System.setProperty("JdbcUrl","jdbc:h2:mem:test_database");
        System.setProperty("JdbcUsername","user");
        System.setProperty("JdbcPassword","");
        ApplicationContext context = new ApplicationContext();
        subj = context.getAccountDao();
    }

    @Test
    void findByIdHappyPath() {
        Account account = new Account();
        account.setAccountType(AccountType.DEBIT);
        account.setBalance(BigDecimal.valueOf(100));
        account.setName("account");
        account.setUserId(user.getId());
        subj.insert(account);

        subj.findById(account.getId());

    }

    @Test
    void insert() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    @Test
    void findUsersAccountById() {
    }

    @Test
    void countUsersAccounts() {
    }
}