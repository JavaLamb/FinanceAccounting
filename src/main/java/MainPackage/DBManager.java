package MainPackage;

import java.sql.*;

public class DBManager {
    void registration(String email, String hashPassword){
        String sql = """
                select * 
                from users
                where email = ? and password = ?;
                """;
            try (Connection connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/postgres",
                    "postgres",
                    "password")){
                try(PreparedStatement pstmt = connection.prepareStatement(sql)){
                    pstmt.setString(1, email);
                    pstmt.setString(2, hashPassword);
                    try(ResultSet rs = pstmt.executeQuery()){
                        if(rs.next()){
                            //User найден, значит регистрация не нужна и мы отправляет в окно авторизации
                        }else{
                            //User не найден, значит делаем регистрацию так какой-то insert в нашу таблицу
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }
}
