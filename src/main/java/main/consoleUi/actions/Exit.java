package main.consoleUi.actions;

import main.consoleUi.MenuAction;
import main.consoleUi.MenuState;

public class Exit implements MenuAction {
    @Override
    public String showText() {
        return "Exit.";
    }

    @Override
    public MenuState execute() {
        return MenuState.EXIT;
    }
}
