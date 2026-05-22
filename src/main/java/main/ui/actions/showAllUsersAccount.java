package main.ui.actions;

import main.dao.AccountDao;
import main.entities.User;
import main.ui.MenuAction;

public class showAllUsersAccount implements MenuAction {
    User user;
    AccountDao accountDao;

    public showAllUsersAccount(User user, AccountDao accountDao) {
        this.user = user;
        this.accountDao = accountDao;
    }

    @Override
    public String showText() {
        return "Show all my account.";
    }

    @Override
    public void execute() {
        accountDao.findUsersAccountById(user.getId());
    }
}
