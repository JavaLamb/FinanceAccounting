package main.dao.Transactional;

import main.dao.DaoFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {

    public void begin() throws SQLException {
        Connection connection = DaoFactory.getConnection();
        connection.setAutoCommit(false);
        ConnectionHolder.set(connection);
    }
    public void commit()throws SQLException{
        Connection connection = ConnectionHolder.get();
        if(connection == null){
            return;
        }
        try{
            connection.commit();
        }finally{
            connection.close();
            ConnectionHolder.remove();
        }
    }
    public void rollback()throws SQLException{
        Connection connection = ConnectionHolder.get();
        if(connection == null){
            return;
        }
        try{
            connection.rollback();
        } catch (SQLException ignored) {
        }finally {
            connection.close();
        }
        ConnectionHolder.remove();
    }
}
