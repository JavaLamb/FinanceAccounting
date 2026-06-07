package main.ui.actions.UserMenu;

import main.entities.Account;
import main.entities.TransactionType;
import main.entities.User;
import main.service.AccountService;
import main.service.TransactionService;
import main.ui.MenuAction;
import main.ui.MenuState;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CreateTransaction implements MenuAction {
    private final Scanner scanner;
    private final TransactionService transactionService;
    private final AccountService accountService;
    private final User user;

    public CreateTransaction(Scanner scanner, TransactionService transactionService, AccountService accountService, User user) {
        this.scanner = scanner;
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.user = user;
    }

    @Override
    public String showText() {
        return "Create transaction";
    }

    @Override
    public MenuState execute() {
        List<Account> accountList = new ArrayList<>(accountService.showAllAccounts(user.getId()));
        System.out.println("Choose Account: ");
        for (int i = 0; i < accountList.size(); i++) {
            System.out.println((i + 1)+". " + (accountList.get(i)).getName());
        }
        Account selectedAccount = accountList.get(Integer.parseInt(scanner.nextLine()) - 1);
        System.out.println("Enter amount of transaction: ");
        BigDecimal amount = transactionService.readBigDecimal(scanner);
        System.out.println("Choose type of transaction:\n1.Income\n2.Expense\n3.Transfer\n");
        switch (Integer.parseInt(scanner.nextLine())) {
            case 1 -> transactionService.createTransaction(TransactionType.INCOME, selectedAccount, amount);
            case 2 -> transactionService.createTransaction(TransactionType.EXPENSE, selectedAccount, amount);
            case 3 -> {
                System.out.println("Enter recipient`s account id: ");
                int recipientId = Integer.parseInt(scanner.nextLine());
                if(accountService.isExist(recipientId)){
                    transactionService.createTransaction(selectedAccount, recipientId, amount);
                }else System.out.println("Recipient account does`t found");
            }
            default -> System.out.println("invalid option");
        }
        return MenuState.CONTINUE;
    }
}
