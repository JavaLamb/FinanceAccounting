package main.ui;

import main.entities.AccountType;
import main.entities.User;
import main.service.AccountService;
import main.service.UserService;

import java.util.Scanner;

public class App {
    Session session = new Session();

    public App(AccountService accountService, UserService userService, Scanner scanner) {
        this.accountService = accountService;
        this.userService = userService;
        this.scanner = scanner;
    }

    Boolean running = true;
    private final AccountService accountService;
    private final UserService userService;
    private final Scanner scanner;

    public void start() {
        while (running) {
            if (session.getCurrentUser() == null) {

                StartMenu startMenu = new StartMenu(scanner,userService,session);
                startMenu.build();
                startMenu.showMenu();
            } else {
                UserMenu userMenu = new UserMenu(accountService, scanner, session.getCurrentUser());
                userMenu.build();
                MenuState result = userMenu.showMenu();
                if (result == MenuState.LOGOUT) {
                    session.setCurrentUser(null);
                } else if (result == MenuState.EXIT) {
                    running = false;
                }
            }
        }
    }
}


