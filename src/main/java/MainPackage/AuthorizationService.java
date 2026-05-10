package MainPackage;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Scanner;

public class AuthorizationService {
     public void auth(){
        String email;
        String userPassword;
        String JBCryptPassword;
        System.out.println("Enter your email: ");
        Scanner scanner = new Scanner(System.in);
        email = scanner.nextLine();
        System.out.println("Enter your password: ");
        userPassword = scanner.nextLine();
        JBCryptPassword = BCrypt.hashpw(userPassword, BCrypt.gensalt());
         System.out.println(JBCryptPassword);
    }
    void registration(){
         Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your email: ");
        String email = scanner.nextLine();
        System.out.println("Enter your password: ");
        String password = scanner.nextLine();
        System.out.println("Enter your password again to confirm: ");
        String secondPassword = scanner.nextLine();
        if(password.equals(secondPassword)){
            // вызываем метод записи в БД
            // и выводим что все прошло удачно и вызываем метод авторизации снова
        }else{
            System.out.println("Passwords doesn`t match");
            registration();
        }
    }
}
