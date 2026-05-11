package MainPackage;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Scanner;

public class AuthorizationService {
    DBManager dbManager = new DBManager();
    Scanner scanner = new Scanner(System.in);

    User auth() {
        User user;
        String email;
        String userPassword;
        String hashPassword;

        System.out.println("Enter your email: ");
        email = scanner.nextLine();

        user = dbManager.getUserByEmail(email);
        if (user != null) {
            hashPassword = user.getHashPassword();
        }else{
            //Что делать если user null?
            System.out.println("User doesn`t exist");
            return null;
        }
        System.out.println("Enter your password: ");
        userPassword = scanner.nextLine();

        if (BCrypt.checkpw(userPassword, hashPassword)) {
            return user;
        }
        System.out.println("Password is wrong");
        return null;
    }

    void registration() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your email    : ");
        String email = scanner.nextLine();
        System.out.println("Enter your password: ");
        String password = scanner.nextLine();
        System.out.println("Enter your password again to confirm: ");
        String secondPassword = scanner.nextLine();
        if (password.equals(secondPassword)) {
            DBManager dbManager = new DBManager();
            String JBCryptPassword = BCrypt.hashpw(password, BCrypt.gensalt());
            dbManager.registration(email, JBCryptPassword);
            // вызываем метод записи в БД
            // и выводим что все прошло удачно и вызываем метод авторизации снова
        } else {
            System.out.println("Passwords doesn`t match");
            registration();
        }
    }
}
