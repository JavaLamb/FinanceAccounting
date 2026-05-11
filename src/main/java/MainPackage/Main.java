package MainPackage;

import java.util.Scanner;

public class Main {
    static void main(String[] args) {
        AuthorizationService as = new AuthorizationService();
        InUserInterface iUI = new InUserInterface();
        System.out.println("Choose option: \n1. Authorization \n2. Registration");
        Scanner scanner = new Scanner(System.in);
        String authOption = scanner.nextLine();
        User user = as.auth();
        if (authOption.equals("1")) {
            if (user != null) {
                //выводим тут наш интерфейс для зашедшего в аккаунт пользователя.
                iUI.UI(user);
            }
        } else if (authOption.equals("2")) {
            as.registration();
        }
    }
}
