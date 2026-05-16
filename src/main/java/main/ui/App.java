package main.ui;

import main.entities.User;
import main.service.AccountLogic;
import main.service.Authorization;

import java.util.Scanner;

public class App {
    private final Scanner scanner = new Scanner(System.in);
    User user = null;
    AccountLogic al = new AccountLogic();
    Boolean running = true;

    public void start() {
        while (running) {
            if (user == null) {
                //тут пользователь не авторизован еще
                showUnLoggedMenu();
            }else{
                System.out.println("тут будет следующий цикл пока запущено но со следующим меню уже соответственно");
                showLoggedMenu(user);
            }
        }
    }

    void showUnLoggedMenu() {
        System.out.println("Choose option: \n1.Authorization\n2.Registration\n3.Close app");
        switch (Integer.parseInt(scanner.nextLine())) {
            case 1 ->this.user = login();
            case 3 -> this.running = false;
                //Метод авторизации, меняем тут LoggedIn на true если авторизация успешна
            //case 2:->  //Метод регистрации
        }
    }
    void showLoggedMenu(User user){
        System.out.println("Choose option as logged user: \n1.Show all accounts\n2.Exit");
        switch(Integer.parseInt(scanner.nextLine())){
            case 1 -> System.out.println(al.showAllAccounts(user));
                //showAllAccounts method
            case 2 -> this.user = null;
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


