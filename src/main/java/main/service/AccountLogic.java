package main.service;

import main.dao.AccountDao;
import main.entities.Account;
import main.entities.User;

import java.util.List;

public class AccountLogic {
    public List<Account> showAllAccounts(User user){
        Integer id = user.getId();
        AccountDao ad = new AccountDao();
        return ad.findUsersAccountById(id);
    }
}
