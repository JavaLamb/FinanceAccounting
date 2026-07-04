package main.consoleUi;

import main.service.AccountService;
import main.service.TransactionService;
import main.service.UserService;
import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;

import java.util.Scanner;

//@Component
@Profile("!test")
public class App {
    Session session = new Session();

    public App(AccountService accountService, TransactionService transactionService, UserService userService, Scanner scanner) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.userService = userService;
        this.scanner = scanner;
    }

    Boolean running = true;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final UserService userService;
    private final Scanner scanner;

    public void start() {
        while (running) {
            if (session.getCurrentUser() == null) {
                StartMenu startMenu = new StartMenu(scanner, userService, session);
                startMenu.build();
                MenuState result = startMenu.showMenu();
                if (result == MenuState.EXIT) {
                    running = false;
                }
            } else {
                UserMenu userMenu = new UserMenu(accountService, scanner, transactionService, session);
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


