package main.ui;

public interface MenuAction<T> {
    String showText();
    MenuState execute();

}
