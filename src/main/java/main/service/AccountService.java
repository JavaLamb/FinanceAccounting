package main.service;

import main.dao.AccountDao;
import main.entities.Account;
import main.entities.AccountType;
import main.entities.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {
    private final AccountDao accountDao;
    int accountLimit = 5;

    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public List<Account> showAllAccounts(int Userid) {
        return accountDao.findUsersAccountById(Userid);
    }

    public boolean canCreateMoreAccount(int id) {
        int result = accountDao.countUsersAccounts(id);
        if (result < 0) {
            return false;
        }
        return result < accountLimit;
    }
    public Account findByIdService(int id){
        return accountDao.findById(id);
    }

    public boolean isExist(int id) {
        return accountDao.findById(id) != null;
    }

    public void createAccount(User user, AccountType accType, String name, BigDecimal balance) {
        accountDao.insert(new Account(name, user, accType, balance));
    }

    public BigDecimal getBalance(int accountId) {
        return accountDao.findById(accountId).getBalance();
    }
}
