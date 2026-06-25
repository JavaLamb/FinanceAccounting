package main.dao;

import main.dao.Transactional.ConnectionHolder;
import main.entities.Account;
import main.entities.AccountType;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AccountDao implements Dao<Account, Integer> {
    private final DataSource dataSource;

    public AccountDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Account> findAll() {
        return List.of();
    }


    @Override
    public Account findById(Integer id) {
        String sql = "select * from accounts where id = ?";
        Connection connection = ConnectionHolder.get();
        if (connection == null) {
            try {
                connection = dataSource.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        try (PreparedStatement pstmnt = connection.prepareStatement(sql)) {
            pstmnt.setInt(1, id);
            try (ResultSet rs = pstmnt.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getBigDecimal("balance"),
                            rs.getInt("userid"),
                            AccountType.valueOf(rs.getString("type"))
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error with AccountDao - findById", e);
        }
        return null;
    }

    @Override
    public Account insert(Account account) {
        String sql = "insert into accounts (name, userid, type, balance) values (?,?,?,?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmnt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmnt.setString(1, account.getName());
            pstmnt.setInt(2, account.getUserId());
            pstmnt.setString(3, String.valueOf(account.getAccountType()));
            pstmnt.setBigDecimal(4, account.getBalance());
            pstmnt.executeUpdate();
            try (ResultSet generatedKeys = pstmnt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int id = generatedKeys.getInt(1);
                    account.setId(id);
                    return account;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error with AccountDao - findUsersAccountById", e);
        }
        return null;
    }

    @Override
    public Account update(Account account) {
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";
        Connection connection = ConnectionHolder.get();
        try (PreparedStatement pstmnt = connection.prepareStatement(sql)) {
            pstmnt.setBigDecimal(1, account.getBalance());
            pstmnt.setInt(2, account.getId());
            pstmnt.executeUpdate();
            try (ResultSet generatedKeys = pstmnt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    account.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error with AccountDao - update", e);
        }
        return null;
    }

    @Override
    public boolean delete(Integer integer) {
        return false;
    }

    public List<Account> findUsersAccountById(Integer id) {
        List<Account> list = new ArrayList<>();
        String sql = "select * from accounts where userid = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmnt = connection.prepareStatement(sql)) {
            pstmnt.setInt(1, id);
            try (ResultSet rs = pstmnt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Account(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getBigDecimal("balance"),
                            rs.getInt("userid"),
                            AccountType.valueOf(rs.getString("type"))));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error with AccountDao - findUsersAccountById", e);
        }
        return list;
    }

    public int countUsersAccounts(int id) {
        String sql = "select count(*) from accounts where userid = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmnt = connection.prepareStatement(sql)) {
            pstmnt.setInt(1, id);
            try (ResultSet rs = pstmnt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error with AccountDao - accLimit", e);
        }
        return -1;
    }
}
