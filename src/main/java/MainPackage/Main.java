package MainPackage;

import java.util.Scanner;

public class Main {
    static void main(String[] args) {
        AuthorizationService as = new AuthorizationService();
        System.out.println("Choose option: \n1. Authorization \n2. Registration");
        Scanner scanner = new Scanner(System.in);
        String authOption = scanner.nextLine();
        if(authOption.equals("1")){
            as.auth();
        }else if (authOption.equals("2")){
            as.registration();
        }
    }
}
