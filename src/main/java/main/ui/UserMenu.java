package main.ui;

import main.dao.AccountDao;
import main.entities.User;
import main.service.AccountService;
import main.service.Authorization;
import main.ui.actions.showAllUsersAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserMenu extends Menu{

    public UserMenu(User user, AccountDao accountDao, Scanner scanner) {
        super(scanner);
        actions.add(new showAllUsersAccount(user, accountDao));

    }
    // Интегрировать с ApplicationContext
    // После переделки удалить старый вариант меню
    //Пишем сюда объекты от которых будут зависеть наши методы AccountService и т.д.
    //Мы их будем получать из нашего ApplicationContext через конструктор.
}
