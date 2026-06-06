package main.ui;

import main.service.UserService;
import main.ui.actions.Authorization;
import main.ui.actions.Exit;

import java.util.Scanner;

public class StartMenu extends Menu{
    private final UserService userService;
    private final Session session;

    public StartMenu(Scanner scanner, UserService userService, Session session) {
        super(scanner);
        this.userService = userService;
        this.session = session;
    }
    public void build(){
        actions.add(new Authorization(userService, scanner, session));
        actions.add(new Exit());
    }
}
