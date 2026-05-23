package main.service;

import main.dao.AccountDao;
import main.entities.Account;
import main.entities.AccountType;
import main.entities.User;

import java.util.List;

public class AccountService {
    AccountDao accountDao;

    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public List<Account> showAllAccounts(int id){
        return accountDao.findUsersAccountById(id);
    }

    public boolean canCreateMoreAccount(int id){
        int result = accountDao.countUsersAccounts(id);
        if(result != -1){
            return result < 5;
        }
        return false;
    }


    public void createAccount(User user, AccountType accType, String name){
        accountDao.insert(new Account(name, user.getId(),accType));
    }
}
