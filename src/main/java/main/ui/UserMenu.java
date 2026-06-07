package main.ui;

import main.entities.User;
import main.service.AccountService;
import main.service.TransactionService;
import main.ui.actions.UserMenu.CreateNewAccount;
import main.ui.actions.Exit;
import main.ui.actions.Logout;
import main.ui.actions.UserMenu.CreateTransaction;
import main.ui.actions.UserMenu.ShowAllUsersAccount;

import java.util.Scanner;

public class UserMenu extends Menu{
private final AccountService accountService;
private final TransactionService transactionService;
User user;

    public UserMenu(AccountService accountService, Scanner scanner, User user, TransactionService transactionService) {
        super(scanner);
        this.transactionService = transactionService;
        this.user = user;
        this.accountService = accountService;
    }

    public void build(){
        actions.add(new ShowAllUsersAccount(accountService, user));
        actions.add(new CreateNewAccount(accountService, user, scanner));
        actions.add(new CreateTransaction(scanner,transactionService, accountService, user));
        actions.add(new Logout());
        actions.add(new Exit());

    }
}
