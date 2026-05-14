package main.dao;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DaoFactory {
    private static DataSource dataSource;

    public static DataSource getDataSource() {
        // not thread-safe
        if(dataSource == null){
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres");
            ds.setUsername("postgresql");
            ds.setPassword("password");
            // Параметры HikariCP можно настроить тут же:
            //maximumPoolSize
            //minimumIdle
            //connectionTimeout
            //validationTimeout
            //idleTimeout
            //maxLifetime
            //keepaliveTime
            //leakDetectionThreshold
            dataSource = ds;
        }
        return dataSource;
    }

    private DaoFactory() {
    }

    public static Connection getConnection() throws SQLException{
        return getDataSource().getConnection();
    }
}
