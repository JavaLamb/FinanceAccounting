package main.ui.actions;

import main.ui.MenuAction;
import main.ui.MenuState;

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
