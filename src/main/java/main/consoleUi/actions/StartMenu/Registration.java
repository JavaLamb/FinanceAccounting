package main.consoleUi.actions.StartMenu;

import main.service.UserService;
import main.consoleUi.MenuAction;
import main.consoleUi.MenuState;

import java.util.Scanner;

public class Registration implements MenuAction {
    private final UserService userService;
    private final Scanner scanner;

    public Registration(UserService userService, Scanner scanner) {
        this.userService = userService;
        this.scanner = scanner;
    }

    @Override
    public String showText() {
        return "Registration.";
    }

    @Override
    public MenuState execute() {
        System.out.println("Enter your email: ");
        String email = scanner.nextLine();
        if(userService.findByEmailService(email) != null){
            System.out.println("User with this email already exist");
            return MenuState.CONTINUE;
        }else{
            System.out.println("Enter your password: ");
            String password = scanner.nextLine();
            userService.createUser(email,password);
        }
        return MenuState.CONTINUE;
    }
}
