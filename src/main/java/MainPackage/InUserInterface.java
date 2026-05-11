package MainPackage;

import java.util.Scanner;

public class InUserInterface {
    Scanner scanner = new Scanner(System.in);
    DBManager dbManager = new DBManager();

    void UI(User user){
        System.out.println("наш UI\n1. Вывести все счета пользователя");
        if(scanner.nextLine().equals("1")){
            //выводится объект, надо переопределить метод toString в моем Account классе
            System.out.println(dbManager.getUserAccountsInfo(user));
         //вызываем метод возвращающий все счета пользователя
        }
    }
}
