package main.dao.Transactional;

import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Service
public class TransactionManager {
    private final DataSource dataSource;

    public TransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void begin() throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        ConnectionHolder.set(connection);
    }

    public void commit() throws SQLException {
        Connection connection = ConnectionHolder.get();
        if (connection == null) {
            return;
        }
        try {
            connection.commit();
        } finally {
            connection.close();
            ConnectionHolder.remove();
        }
    }

    public void rollback() throws SQLException {
        Connection connection = ConnectionHolder.get();
        if (connection == null) {
            return;
        }
        try {
            connection.rollback();
        } catch (SQLException ignored) {
        } finally {
            connection.close();
        }
        ConnectionHolder.remove();
    }
}
