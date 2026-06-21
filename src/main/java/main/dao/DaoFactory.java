package main.dao;

import com.zaxxer.hikari.HikariDataSource;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.command.CommandScope;
import liquibase.command.core.ChangelogSyncCommandStep;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DaoFactory {
    private static DataSource dataSource;

    public static DataSource getDataSource() {
        // not thread-safe
        if(dataSource == null){
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(System.getProperty("JdbcUrl","jdbc:postgresql://localhost:5432/postgres"));
            ds.setUsername(System.getProperty("JdbcUsername","postgres"));
            ds.setPassword(System.getProperty("JdbcPassword","password"));
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
            initDataBase(ds);
        }
        return dataSource;
    }

    private DaoFactory() {
    }

    public static Connection getConnection() throws SQLException{
        return getDataSource().getConnection();
    }

    public static void initDataBase(DataSource dataSource){
        try {
            DatabaseConnection connection = new JdbcConnection(dataSource.getConnection());
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(connection);
            Liquibase liquibase = new Liquibase(
                    "liquibase.xml",
                    new ClassLoaderResourceAccessor(),
                    database
            );
            liquibase.update(new Contexts());
        } catch (SQLException | LiquibaseException e) {
            throw new RuntimeException(e);
        }
    }
}
