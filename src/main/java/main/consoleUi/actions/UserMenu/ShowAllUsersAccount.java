package main.consoleUi.actions.UserMenu;

import main.entities.User;
import main.service.AccountService;
import main.consoleUi.MenuAction;
import main.consoleUi.MenuState;

public class ShowAllUsersAccount implements MenuAction {
    AccountService accountService;
    User user;

    public ShowAllUsersAccount(AccountService accountService, User user) {
        this.accountService = accountService;
        this.user = user;
    }

    @Override
    public String showText() {
        return "Show all my account.";
    }

    @Override
    public MenuState execute() {
        accountService.showAllAccounts(user.getId()).forEach(System.out::println);
        return MenuState.CONTINUE;
    }

}
