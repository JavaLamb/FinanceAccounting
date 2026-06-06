package main.ui.actions.UserMenu;

import main.ui.MenuAction;
import main.ui.MenuState;

public class CreateTransaction implements MenuAction {
    @Override
    public String showText() {
        return "Create transaction";
    }

    @Override
    public MenuState execute() {
        System.out.println("Choose type of transaction: ");
        return MenuState.CONTINUE;
    }
}
