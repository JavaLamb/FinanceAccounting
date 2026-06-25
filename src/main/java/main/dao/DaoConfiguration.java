package main.dao;

import com.zaxxer.hikari.HikariDataSource;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class DaoConfiguration {
    @Bean
    public DataSource dataSource(Environment environment){
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(environment.getProperty("JdbcUrl","jdbc:postgresql://localhost:5432/postgres"));
        hikariDataSource.setUsername(environment.getProperty("JdbcUsername","postgres"));
        hikariDataSource.setPassword(environment.getProperty("JdbcPassword","password"));
        return hikariDataSource;
    }
    @Bean
    public Liquibase liquibase(DataSource dataSource) throws LiquibaseException, SQLException {
        DatabaseConnection connection = new JdbcConnection(dataSource.getConnection());
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(connection);
        Liquibase liquibase = new Liquibase(
                "liquibase.xml",
                new ClassLoaderResourceAccessor(),
                database
        );
        liquibase.update(new Contexts());
        return liquibase;
    }
}
