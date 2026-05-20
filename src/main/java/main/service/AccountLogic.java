package main.service;

import main.dao.AccountDao;
import main.entities.Account;
import main.entities.AccountType;
import main.entities.User;

import java.sql.SQLOutput;
import java.util.List;

public class AccountLogic {
    AccountDao ad = new AccountDao();

    public List<Account> showAllAccounts(User user){
        Integer id = user.getId();
        return ad.findUsersAccountById(id);
    }

    public boolean canCreateMoreAccount(int id){
        int result = ad.countUsersAccounts(id);
        if(result != -1){
            return result < 5;
        }
        return false;
    }


    public void createAccount(User user, AccountType accType, String name){
        ad.insert(new Account(name, user.getId(),accType));
    }
}
