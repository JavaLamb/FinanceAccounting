package main.consoleUi.actions.StartMenu;

import main.entities.User;
import main.service.UserService;
import main.consoleUi.MenuAction;
import main.consoleUi.MenuState;
import main.consoleUi.Session;

import java.util.Scanner;

public class Authorization implements MenuAction {
    private final UserService userService;
    private final Scanner scanner;
    private final Session session;
    public Authorization(UserService userService, Scanner scanner, Session session) {
        this.userService = userService;
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
            user = userService.findByEmailService(email);
        } catch (RuntimeException e) {
            System.out.println("Пользователь не найден");
            return null;
        }
        if (user != null) {
            System.out.println("Введите пароль: ");
            String password = scanner.nextLine();
            if(userService.checkPassword(password,user)){
                session.setCurrentUser(user);
                return MenuState.CONTINUE;
            }else{
                System.out.println("Password is wrong");
            }
        }
        return MenuState.CONTINUE;
    }
}
