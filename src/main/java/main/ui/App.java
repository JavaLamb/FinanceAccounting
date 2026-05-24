package main.ui;

import main.entities.AccountType;
import main.entities.User;
import main.service.AccountService;
import main.service.AuthorizationService;

import java.util.Scanner;

public class App {

    public App(AccountService accountService, AuthorizationService authorizationService, Scanner scanner) {

        this.accountService = accountService;
        this.authorizationService = authorizationService;
        this.scanner = scanner;
    }

    User user = null;
    Boolean running = true;
    private final AccountService accountService;
    private final AuthorizationService authorizationService;
    private final Scanner scanner;

    public void start() {
        while (running) {
            if (user == null) {
                //тут пользователь не авторизован еще
                showUnLoggedMenu();
            } else {
                UserMenu userMenu = new UserMenu(accountService, scanner, user);
                userMenu.build();
                MenuState result = userMenu.showMenu();
                if (result == MenuState.LOGOUT) {
                    user = null;
                } else if (result == MenuState.EXIT) {
                    running = false;
                }
            }
        }
    }

    void showUnLoggedMenu() {
        System.out.println("Choose option: \n1.Authorization\n2.Registration\n3.Close app");
        switch (Integer.parseInt(scanner.nextLine())) {
            case 1 -> this.user = login();
            case 3 -> this.running = false;
            //case 2:->  //Метод регистрации
        }
    }

    void showLoggedMenu(User user) {
        System.out.println("Choose option accountService logged user: \n1.Show all accounts\n2.Create Account\n3.Exit");
        switch (Integer.parseInt(scanner.nextLine())) {
            case 1 -> accountService.showAllAccounts(user.getId()).forEach(System.out::println);
            case 2 -> {
                if (accountService.canCreateMoreAccount(user.getId())) {
                    System.out.println("Choose type of account:\n1.Debit\n2.Credit\n3.Savings\n4.Back");
                    AccountType at = switch (Integer.parseInt(scanner.nextLine())) {
                        case 1 -> at = AccountType.DEBIT;
                        case 2 -> at = AccountType.CREDIT;
                        case 3 -> at = AccountType.SAVINGS;
                        default -> at = null;
                    };
                    if (at != null) {
                        System.out.println("Enter account name: ");
                        String name = scanner.nextLine();
                        accountService.createAccount(user, at, name);
                    } else {
                        System.out.println("invalid option");
                    }
                } else {
                    System.out.println("You have reached your account limit");
                }
            }
            case 3 -> this.user = null;
        }
    }

    User login() {
        System.out.println("Введите email");
        String email = scanner.nextLine();
        //В идеале сюда запихать emailValidator
        System.out.println("Введите password");
        String password = scanner.nextLine();
        //Передаем логин и пароль в сервис авторизации и ждем возвращаемого user
        User user = authorizationService.auth(email, password);
        this.user = user;
        return user;
    }
}


