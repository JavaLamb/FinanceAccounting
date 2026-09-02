package main.dao;

import main.entities.User;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDao implements Dao<User, Integer> {
    private final DataSource dataSource;

    public UserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public User findById(Integer integer) {
        try (Connection connection = dataSource.getConnection()) {
            System.out.println("findById не работает");
        } catch (SQLException e) {
            throw new RuntimeException("Error with findById", e);
        }
        return null;
    }

    @Override
    public User insert(User user) {
        String sql = "insert into users (email, password) VALUES (?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmnt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmnt.setString(1, user.getEmail());
            pstmnt.setString(2, user.getHashPassword());
            pstmnt.executeUpdate();
            try (ResultSet generatedKeys = pstmnt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error with UserDao - insert", e);
        }
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

    public Optional<User> findByEmail(String email) {
        String sql = "select * from users where email = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmnt = connection.prepareStatement(sql)) {
            pstmnt.setString(1, email);
            try (ResultSet rs = pstmnt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new User(rs.getInt("id"), rs.getString("email"), rs.getString("password")));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error with UserDao - findByEmail", e);
        }
        return Optional.empty();
    }
}
