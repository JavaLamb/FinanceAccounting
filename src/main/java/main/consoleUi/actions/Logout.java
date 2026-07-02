package main.consoleUi.actions;

import main.consoleUi.MenuAction;
import main.consoleUi.MenuState;

public class Logout implements MenuAction {
    @Override
    public String showText() {
        return "Logout.";
    }

    @Override
    public MenuState execute() {
        return MenuState.LOGOUT;
    }
}
