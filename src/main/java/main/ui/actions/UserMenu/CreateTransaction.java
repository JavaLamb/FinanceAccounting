package main.ui.actions.UserMenu;

import main.entities.Account;
import main.entities.TransactionCategory;
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
        try {
            List<Account> accountList = new ArrayList<>(accountService.showAllAccounts(user.getId()));
            System.out.println("Choose Account: ");
            for (int i = 0; i < accountList.size(); i++) {
                System.out.println((i + 1) + ". " + (accountList.get(i)).getName());
            }
            Account selectedAccount = accountList.get(Integer.parseInt(scanner.nextLine()) - 1);
            System.out.println("Enter amount of transaction: ");
            System.out.println("Your balance: " + accountService.getBalance(selectedAccount.getId()));
            BigDecimal amount = transactionService.readBigDecimal(scanner);
            System.out.println("Choose transaction_category: \n");
            List<TransactionCategory> categoryList = new ArrayList<>(transactionService.showCategories(user.getId()));
            for (int i = 0; i < categoryList.size(); i++) {
                System.out.println((i + 1) + ". " + categoryList.get(i));
            }
            int selectedCategory = categoryList.get(Integer.parseInt(scanner.nextLine())-1).getId();
            System.out.println("Choose type of transaction:\n1.Income\n2.Expense\n3.Transfer\n");
            switch (Integer.parseInt(scanner.nextLine())) {
                case 1 -> transactionService.createTransaction(TransactionType.INCOME, selectedAccount, amount, selectedCategory);
                case 2 -> transactionService.createTransaction(TransactionType.EXPENSE, selectedAccount, amount, selectedCategory);
                case 3 -> {
                    System.out.println("Enter recipient`s account id: ");
                    int recipientId = Integer.parseInt(scanner.nextLine());
                    if (accountService.isExist(recipientId)) {
                        transactionService.createTransaction(selectedAccount, accountService.findByIdService(recipientId), amount, selectedCategory);
                    } else System.out.println("Recipient account does`t found");
                }
                default -> System.out.println("invalid option");
            }
        } catch (RuntimeException e) {
            System.out.println("Exception was found in CreateTransaction");
        }
        return MenuState.CONTINUE;
    }
}
