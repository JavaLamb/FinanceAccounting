package main.dao;

import main.dao.Transactional.ConnectionHolder;
import main.entities.Transaction;
import main.entities.TransactionCategory;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TransactionDao implements Dao<Transaction, Integer> {
    private final DataSource dataSource;

    public TransactionDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

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
        String sql = "insert into transactions (from_account_id, to_account_id, category_id, amount) values (?,?,?,?)";
        Connection connection = ConnectionHolder.get();
        try (PreparedStatement pstmnt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmnt.setObject(1, transaction.getFromAccountId());
            pstmnt.setObject(2, transaction.getToAccountId());
            pstmnt.setInt(3, transaction.getCategoryId());
            pstmnt.setBigDecimal(4, transaction.getAmount());
            pstmnt.executeUpdate();
            try (ResultSet generatedKeys = pstmnt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    transaction.setId(generatedKeys.getInt(1));
                    return transaction;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error with AccountDao - findUsersAccountById", e);
        }
        return null;
    }

    public TransactionCategory insertCategory(TransactionCategory category) {
        String sql = "insert into transaction_category (transaction_name, userid) values (?,?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmnt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmnt.setString(1, category.getTransactionCategoryName());
            pstmnt.setInt(2, category.getUserid());
            pstmnt.executeUpdate();
            try (ResultSet generatedKeys = pstmnt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    category.setId(generatedKeys.getInt(1));
                    return category;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error with AccountDao - findUsersAccountById", e);
        }
        return null;
    }

    public List<TransactionCategory> showCategoriesDao(int userid) {
        List<TransactionCategory> list = new ArrayList<>();
        String sql = "select * from transaction_category where userid = ? or userid is null";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmnt = connection.prepareStatement(sql)) {
            pstmnt.setInt(1, userid);
            try (ResultSet rs = pstmnt.executeQuery()) {
                while (rs.next()) {
                    list.add(new TransactionCategory(
                            rs.getString("transaction_name"),
                            rs.getInt("id")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Error with transactionDao - showCategoriesDao", e);
        }
        return list;
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
