package main.dao;

import main.entities.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static main.dao.DaoFactory.getConnection;

public class AccountDao implements Dao<Account, Integer> {
    @Override
    public List<Account> findAll() {
        return List.of();
    }

    @Override
    public Account findById(Integer id) {
        String sql = "select * from accounts where userid = ?";
        try(Connection connection = getConnection();
            PreparedStatement pstmnt = connection.prepareStatement(sql)){
            pstmnt.setInt(1,id);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error with AccountDao - findById", e);
        }
        return null;
    }

    @Override
    public Account insert(Account account) {
        return null;
    }

    @Override
    public Account update(Account account) {
        return null;
    }

    @Override
    public boolean delete(Integer integer) {
        return false;
    }

    public List<Account> findUsersAccountById(Integer id) {
        List<Account> list = new ArrayList<Account>();
        String sql = "select * from accounts where userid = ?";
        try(Connection connection = getConnection();
            PreparedStatement pstmnt = connection.prepareStatement(sql)){
            pstmnt.setInt(1,id);
            try(ResultSet rs = pstmnt.executeQuery()){
                while(rs.next()){
                    list.add(new Account(rs.getInt("id"),rs.getString("name"),rs.getBigDecimal("balance"),rs.getInt("userid")));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error with AccountDao - findUsersAccountById", e);
        }
        return list;
    }
}
