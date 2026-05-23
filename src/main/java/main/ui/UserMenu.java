package main.ui;

import main.entities.User;
import main.service.AccountService;
import main.ui.actions.ShowAllUsersAccount;

import java.util.Scanner;

public class UserMenu extends Menu{
AccountService accountService;
private final ShowAllUsersAccount showAllUsersAccount;
User user;

    public UserMenu(AccountService accountService, Scanner scanner) {
        super(scanner);
        showAllUsersAccount = new ShowAllUsersAccount(accountService);
        //Добавляем все пункты меню
        actions.add(showAllUsersAccount);
    }
    public void setUser(User user) {
        showAllUsersAccount.setUser(user);
    }

//    public void build(User user){
//        showAllUsersAccount.setUser(user);
//        actions.add(new ShowAllUsersAccount(this.accountService));
//    }
    // Интегрировать с ApplicationContext
    // После переделки удалить старый вариант меню
}
