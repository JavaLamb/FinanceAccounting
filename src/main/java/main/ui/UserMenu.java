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
//        showAllUsersAccount = new ShowAllUsersAccount(accountService, user);
    }

    public void build(){
        actions.add(new ShowAllUsersAccount(accountService, user));
        actions.add(new CreateNewAccount(accountService, user, scanner));
        actions.add(new Logout());
        actions.add(new Exit());

    }

//    public void build(User user){
//        showAllUsersAccount.setUser(user);
//        actions.add(new ShowAllUsersAccount(this.accountService));
//    }
    // Интегрировать с ApplicationContext
    // После переделки удалить старый вариант меню
}
