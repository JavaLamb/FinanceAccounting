package main.consoleUi;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

abstract public class Menu {
    protected List<MenuAction> actions = new ArrayList<>();
    protected Scanner scanner;

    public Menu(Scanner scanner) {
        this.scanner = scanner;
    }

    protected MenuState showMenu(){
        for (int i = 0; i < actions.size(); i++) {
            System.out.println((i + 1) + ". " + actions.get(i).showText());
        }
        int choice = Integer.parseInt(scanner.nextLine());
        return actions.get(choice - 1).execute();
    }
    abstract public void build();
}
