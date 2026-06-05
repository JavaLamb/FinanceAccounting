package main.ui.actions;

import main.dao.UserDao;
import main.entities.User;
import main.ui.MenuAction;
import main.ui.MenuState;
import main.ui.Session;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Scanner;

public class Authorization implements MenuAction {
    private final UserDao userDao;
    private final Scanner scanner;
    private final Session session;
    public Authorization(UserDao userDao, Scanner scanner, Session session) {
        this.userDao = userDao;
        this.scanner = scanner;
        this.session = session;
    }

    @Override
    public String showText() {
        return "Authorization.";
    }

    @Override
    public MenuState execute() {
        User user = null;
        System.out.println("Введите email: ");
        String email = scanner.nextLine();
        try {
            user = userDao.findByEmail(email);
        } catch (RuntimeException e) {
            System.out.println("Пользователь не найден");
            return null;
        }
        if (user != null) {
            System.out.println("Введите пароль: ");
            String password = scanner.nextLine();
            String hash = user.getHashPassword();
            if (BCrypt.checkpw(password, hash)) {
                session.setCurrentUser(user);
                return MenuState.CONTINUE;
            }else{
                System.out.println("Password is wrong");
            }
        }
        return MenuState.CONTINUE;
    }
}
