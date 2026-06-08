package main.dao;

import main.entities.Account;
import main.entities.AccountType;
import main.entities.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static main.dao.DaoFactory.getConnection;

public class TransactionDao implements Dao<Transaction, Integer> {
    @Override
    public List<Transaction> findAll() {
        return List.of();
    }

    @Override
    public Transaction findById(Integer integer) {
        return null;
    }

    @Override
    public Transaction insert(Transaction transaction) {
        String sql = "insert into transactions (from_account_id, to_account_id, category_id, amount) values (?,?,?,?) returning *";
        try (Connection connection = getConnection();
             PreparedStatement pstmnt = connection.prepareStatement(sql)) {
            pstmnt.setObject(1, transaction.getFromAccountId());
            pstmnt.setObject(2, transaction.getToAccountId());
            pstmnt.setInt(3, transaction.getCategoryId());
            pstmnt.setBigDecimal(4, transaction.getAmount());
            try (ResultSet rs = pstmnt.executeQuery()) {
                if (rs.next()) {
                    return (new Transaction(
                            rs.getInt("id"),
                            rs.getInt("from_account_id"),
                            rs.getInt("to_account_id"),
                            rs.getInt("category_id"),
                            rs.getBigDecimal("amount"),
                            rs.getObject("date_time", LocalDateTime.class)
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error with AccountDao - findUsersAccountById", e);
        }
        return null;
    }

    @Override
    public Transaction update(Transaction transaction) {
        return null;
    }

    @Override
    public boolean delete(Integer integer) {
        return false;
    }
}
