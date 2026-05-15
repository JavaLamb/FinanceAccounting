package main.ui;

import main.entities.User;
import main.service.Authorization;

import java.util.Scanner;

public class App {
    private final Scanner scanner = new Scanner(System.in);
    User user = null;

    public void start() {
        Boolean running = true;//Пока не уверен как эту штуку правильно завернуть на закрытие программы и где менять её на false
        while (running) {
            if (user == null) {
                //тут пользователь не авторизован еще
                showUnLoggedMenu();
            }else{
                System.out.println("тут будет следующий цикл пока запущено но со следующим меню уже соответственно");
            }
        }
    }

    void showUnLoggedMenu() {
        System.out.println("Choose option: \n1.Authorization\n2.Registration");
        switch (Integer.parseInt(scanner.nextLine())) {
            case 1:
                this.user = login();
                break;//Метод авторизации, меняем тут LoggedIn на true если авторизация успешна
            case 2:  //Метод регистрации
        }
    }

    User login(){
        System.out.println("Введите email");
        String email = scanner.nextLine();
        System.out.println("Введите password");
        String password = scanner.nextLine();
        //Передаем логин и пароль в сервис авторизации и ждем возвращаемого user
        Authorization authorization = new Authorization();
        return authorization.auth(email, password);
    }
}


