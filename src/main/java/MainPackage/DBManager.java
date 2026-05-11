package MainPackage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
    void registration(String email, String hashPassword) {
        String addSql = """
                insert
                into users(email, password)
                values (?,?)
                """;
        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/postgres",
                "postgres",
                "password")) {
            if (userIsExist(email)) {
                System.out.println("User is already exist");
                AuthorizationService as = new AuthorizationService();
                as.auth();
                //User найден, значит регистрация не нужна и мы отправляет в окно
                // авторизации. А вообще-то нужно убрать отсюда if-ы и сделать так чтобы метод возвращал
                // булеан, и тогда уже application layer будет исходя из возвращенного значения выдавать
                // информацию пользователю и переключаться между окнами. DBManager не должен открывать
                // окна
            } else {
                try (PreparedStatement addStatement = connection.prepareStatement(addSql)) {
                    addStatement.setString(1, email);
                    addStatement.setString(2, hashPassword);
                    System.out.println("does this work?");
                    addStatement.executeUpdate();
                }
                //User не найден, значит делаем регистрацию -> какой-то insert в нашу таблицу
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    boolean userIsExist(String email) {
        String sql = """
                select 1
                from users
                where email = ?
                """;
        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/postgres",
                "postgres",
                "password");
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    //Значит существует юзер с таким email
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    User getUserByEmail(String email) {
        String sql = """
                select *
                from users
                where email = ?
                """;
        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/postgres",
                "postgres",
                "password");
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getString(1),rs.getString(2),rs.getString(3));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    List<Account> getUserAccountsInfo(User user){
        String uEmail = user.getEmail();
        String sql = """
                select * from accounts as a join users as u on u.id = a.userid
                where email = ?
                """;
        try(Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres",
                "postgres",
                "password");
        PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setString(1,uEmail);
            try(ResultSet rs = preparedStatement.executeQuery()){
                List<Account> listOfAccount = new ArrayList<>();
                while(rs.next()){
                    listOfAccount.add(new Account(rs.getString(1),rs.getString(2),rs.getString(3), rs.getString(4)));
                }
                return listOfAccount;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}