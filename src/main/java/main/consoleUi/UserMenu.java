package main.consoleUi;

import main.service.AccountService;
import main.service.TransactionService;
import main.consoleUi.actions.UserMenu.CreateNewAccount;
import main.consoleUi.actions.Exit;
import main.consoleUi.actions.Logout;
import main.consoleUi.actions.UserMenu.CreateTransaction;
import main.consoleUi.actions.UserMenu.ShowAllUsersAccount;

import java.util.Scanner;

public class UserMenu extends Menu{
private final AccountService accountService;
private final TransactionService transactionService;
private final Session session;
    public UserMenu(AccountService accountService, Scanner scanner, TransactionService transactionService, Session session) {
        super(scanner);
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.session = session;
    }

    public void build(){
        actions.add(new ShowAllUsersAccount(accountService, session.getCurrentUser()));
        actions.add(new CreateNewAccount(accountService, session.getCurrentUser(), scanner));
        actions.add(new CreateTransaction(scanner,transactionService, accountService, session.getCurrentUser()));
        actions.add(new Logout());
        actions.add(new Exit());

    }
}
