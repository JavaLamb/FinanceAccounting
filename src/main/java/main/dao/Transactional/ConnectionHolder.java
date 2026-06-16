package main.dao.Transactional;

import java.sql.Connection;

public class ConnectionHolder {
    private static final ThreadLocal<Connection> CONNECTION_THREAD_LOCAL = new ThreadLocal<>();

    public static void set(Connection connection){
        CONNECTION_THREAD_LOCAL.set(connection);
    }

    public static Connection get(){
        return CONNECTION_THREAD_LOCAL.get();
    }

    public static void remove(){
        CONNECTION_THREAD_LOCAL.remove();
    }
}
