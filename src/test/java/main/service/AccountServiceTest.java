package main.service;

import main.dao.AccountDao;
import main.dao.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AccountServiceTest {
    AccountDao accountDao;
    AccountService subj;
    int accountLimit;


    @BeforeEach
    void setUp() {
        accountDao = mock(AccountDao.class);
        subj = new AccountService(accountDao);
        accountLimit = subj.accountLimit;
    }

    @Nested
    class CanCreateMoreAccountTests {
        @Test
        void ReturnFalseWhenAccountCountIsExactlyLimit() {
            when(accountDao.countUsersAccounts(1)).thenReturn(accountLimit);
            assertFalse(subj.canCreateMoreAccount(1));
        }

        @Test
        void returnTrueWhenAccountCountOneLessThanLimit() {
            when(accountDao.countUsersAccounts(1)).thenReturn(accountLimit - 1);
            assertTrue(subj.canCreateMoreAccount(1));
        }

        @Test
        void returnFalseWhenAccountCountIsOneMoreThanLimit() {
            when(accountDao.countUsersAccounts(1)).thenReturn(accountLimit + 1);
            assertFalse(subj.canCreateMoreAccount(1));
        }

        @Test
        void returnFalseWhenDaoCountUsersReturnedError() {
            when(accountDao.countUsersAccounts(1)).thenReturn(-1);
            assertFalse(subj.canCreateMoreAccount(1));
        }
    }
}