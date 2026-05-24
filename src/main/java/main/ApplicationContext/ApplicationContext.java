package main.ApplicationContext;

import main.dao.AccountDao;
import main.dao.UserDao;
import main.service.AccountService;
import main.service.AuthorizationService;
import main.ui.App;
import main.ui.UserMenu;

import java.util.Scanner;

public class ApplicationContext {
    private final Scanner scanner = new Scanner(System.in);
    //Dao
    private final AccountDao accountDao =  new AccountDao();
    private final UserDao userDao = new UserDao();
    //Services
    private final AuthorizationService authorizationService = new AuthorizationService(getUserDao());
    private final AccountService accountService = new AccountService(getAccountDao());

    private final App app = new App(getAccountService(),getAuthorization(),getScanner());

    public UserDao getUserDao() {
        return userDao;
    }

    public AccountDao getAccountDao() {
        return accountDao;
    }

    public AuthorizationService getAuthorization() {
        return authorizationService;
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
