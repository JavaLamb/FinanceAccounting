package main.ui.actions;

import main.ui.MenuAction;
import main.ui.MenuState;

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
