package main.ui;

import main.entities.User;
import main.service.AccountService;
import main.ui.actions.CreateNewAccount;
import main.ui.actions.Exit;
import main.ui.actions.Logout;
import main.ui.actions.ShowAllUsersAccount;

import java.util.Scanner;

public class UserMenu extends Menu{
private final AccountService accountService;
User user;

    public UserMenu(AccountService accountService, Scanner scanner, User user) {
        super(scanner);
        this.user = user;
        this.accountService = accountService;
    }

    public void build(){
        actions.add(new ShowAllUsersAccount(accountService, user));
        actions.add(new CreateNewAccount(accountService, user, scanner));
        actions.add(new Logout());
        actions.add(new Exit());
    }
}
