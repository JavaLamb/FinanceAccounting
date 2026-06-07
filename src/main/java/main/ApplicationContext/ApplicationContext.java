package main.ApplicationContext;

import main.dao.AccountDao;
import main.dao.TransactionDao;
import main.dao.UserDao;
import main.service.AccountService;
import main.service.TransactionService;
import main.service.UserService;
import main.ui.App;

import java.util.Scanner;

public class ApplicationContext {
    private final Scanner scanner = new Scanner(System.in);
    //Dao
    private final AccountDao accountDao = new AccountDao();
    private final UserDao userDao = new UserDao();
    private final TransactionDao transactionDao = new TransactionDao();
    //Services
    private final UserService userService = new UserService(getUserDao());
    private final AccountService accountService = new AccountService(getAccountDao());
    private final TransactionService transactionService = new TransactionService(getTransactionDao(), getAccountService());

    private final App app = new App(getAccountService(), getTransactionService(), getUserService(), getScanner());

    public UserDao getUserDao() {
        return userDao;
    }

    public AccountDao getAccountDao() {
        return accountDao;
    }

    public TransactionDao getTransactionDao() {
        return transactionDao;
    }

    public UserService getUserService() {
        return userService;
    }

    public TransactionService getTransactionService() {
        return transactionService;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public App getApp() {
        return app;
    }

    public Scanner getScanner() {
        return scanner;
    }
}
