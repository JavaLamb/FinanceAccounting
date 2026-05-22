package main.ApplicationContext;

import main.dao.AccountDao;
import main.dao.UserDao;
import main.service.AccountService;
import main.service.Authorization;
import main.ui.App;

import java.util.Scanner;

public class ApplicationContext {
    private final Scanner scanner = new Scanner(System.in);
    //Dao
    private final AccountDao accountDao =  new AccountDao();
    private final UserDao userDao = new UserDao();
    //Services
    private final Authorization authorization = new Authorization(getUserDao());
    private final AccountService accountService = new AccountService(getAccountDao());

    private final App app = new App(getAccountService(),getAuthorization(),getScanner());

    public UserDao getUserDao() {
        return userDao;
    }

    public AccountDao getAccountDao() {
        return accountDao;
    }

    public Authorization getAuthorization() {
        return authorization;
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
