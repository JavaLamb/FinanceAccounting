package main.dao;

import main.entities.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static main.dao.DaoFactory.getConnection;

public class UserDao implements Dao<User, Integer> {


    @Override
    public List<User> findByAll() {
        return List.of();
    }

    @Override
    public User findById(Integer integer) {
        try(Connection connection = getConnection()){
            //Тут у нас будет запрос, rs и возврат User объекта
            //Что-то надо придумать с исключением как его ловить нормально и обрабатывать
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
}
