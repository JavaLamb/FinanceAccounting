package main.ui;

import main.entities.AccountType;
import main.entities.User;
import main.service.AccountLogic;
import main.service.Authorization;

import java.util.Scanner;

public class App {
    private final Scanner scanner = new Scanner(System.in);
    User user = null;
    AccountLogic al = new AccountLogic();
    Boolean running = true;

    public void start() {
        while (running) {
            if (user == null) {
                //тут пользователь не авторизован еще
                showUnLoggedMenu();
            } else {
                System.out.println("тут будет следующий цикл пока запущено но со следующим меню уже соответственно");
                showLoggedMenu(user);
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
        System.out.println("Choose option as logged user: \n1.Show all accounts\n2.Create Account\n3.Exit");
        switch (Integer.parseInt(scanner.nextLine())) {
            case 1 -> al.showAllAccounts(user).forEach(System.out::println);
            case 2 -> {
                if (al.canCreateMoreAccount(user.getId())) {
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
                        al.createAccount(user, at, name);
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
        Authorization authorization = new Authorization();
        return authorization.auth(email, password);
    }
}


