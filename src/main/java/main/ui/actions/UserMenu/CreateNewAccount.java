package main.ui.actions.UserMenu;

import main.entities.AccountType;
import main.entities.User;
import main.service.AccountService;
import main.ui.MenuAction;
import main.ui.MenuState;

import java.math.BigDecimal;
import java.util.Scanner;

public class CreateNewAccount implements MenuAction {
    private final AccountService accountService;
    User user;
    private final Scanner scanner;

    public CreateNewAccount(AccountService accountService, User user, Scanner scanner) {
        this.accountService = accountService;
        this.user = user;
        this.scanner = scanner;
    }

    @Override
    public String showText() {
        return "Create new account.";
    }

    @Override
    public MenuState execute() {
        {
            if (accountService.canCreateMoreAccount(user.getId())) {
                System.out.println("Choose type of account:\n1.Debit\n2.Credit\n3.Savings\n4.Back");
                AccountType at = switch (Integer.parseInt(scanner.nextLine())) {
                    case 1 -> AccountType.DEBIT;
                    case 2 -> AccountType.CREDIT;
                    case 3 -> AccountType.SAVINGS;
                    default -> null;
                };
                if (at != null) {
                    System.out.println("Enter account name: ");
                    String name = scanner.nextLine();
                    accountService.createAccount(user, at, name, BigDecimal.valueOf(0));
                } else {
                    System.out.println("invalid option");
                }
            } else {
                System.out.println("You have reached your account limit");
            }
            return MenuState.CONTINUE;
        }
    }

}
