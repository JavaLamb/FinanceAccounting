package main.dao;

import main.entities.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static main.dao.DaoFactory.getConnection;

public class UserDao implements Dao<User, Integer> {


    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public User findById(Integer integer){
        try(Connection connection = getConnection()){
            //Тут у нас будет запрос, rs и возврат User объекта
            //Что-то надо придумать с исключением как его ловить нормально и обрабатывать
        } catch (SQLException e) {
            throw new RuntimeException("Error with findById", e);
        }
        return null;
    }

    @Override
    public User insert(User user) {
        return null;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public boolean delete(Integer integer) {
        return false;
    }

    public User findByEmail(String email){
        String sql = "select * from users where email = ?";
        try(Connection connection = getConnection();
        PreparedStatement pstmnt = connection.prepareStatement(sql)){
            pstmnt.setString(1,email);
            try(ResultSet rs = pstmnt.executeQuery()){
                while(rs.next()){
                    System.out.println("мы нашли!!!! We`re in UserDao right now. Can delete this string btw");
                    return new User(rs.getInt("id"), rs.getString("email"), rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error with UserDao - findById", e);
        }
        System.out.println("Пользователь не найден");
        return null;
    }
}
