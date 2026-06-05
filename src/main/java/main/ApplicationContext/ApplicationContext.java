package main.ApplicationContext;

import main.dao.AccountDao;
import main.dao.UserDao;
import main.entities.User;
import main.service.AccountService;
import main.service.UserService;
import main.ui.App;
import main.ui.UserMenu;

import java.util.Scanner;

public class ApplicationContext {
    private final Scanner scanner = new Scanner(System.in);
    //Dao
    private final AccountDao accountDao =  new AccountDao();
    private final UserDao userDao = new UserDao();
    //Services
    private final UserService userService = new UserService(getUserDao());
    private final AccountService accountService = new AccountService(getAccountDao());

    private final App app = new App(getAccountService(),getUserService(),getScanner());

    public UserDao getUserDao() {
        return userDao;
    }

    public AccountDao getAccountDao() {
        return accountDao;
    }

    public UserService getUserService() {
        return userService;
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
