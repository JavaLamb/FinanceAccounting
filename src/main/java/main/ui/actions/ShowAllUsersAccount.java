package main.ui.actions;

import main.entities.User;
import main.service.AccountService;
import main.ui.MenuAction;

public class ShowAllUsersAccount implements MenuAction {
    User user;
    AccountService accountService;

    public ShowAllUsersAccount(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public String showText() {
        return "Show all my account.";
    }

    @Override
    public void execute() {
        accountService.showAllAccounts(user.getId()).forEach(System.out::println);
    }

    public void setUser(User user) {
        this.user = user;
    }
}
