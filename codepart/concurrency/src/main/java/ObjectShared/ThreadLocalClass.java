package ObjectShared;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author zhengzebiao
 * @date 2019/12/22 16:19
 */
public class ThreadLocalClass {

    private static ThreadLocal<Connection> connectionHolder =
            new ThreadLocal<Connection>(){
                @Override
                public Connection initialValue(){
                    try {
                        return DriverManager.getConnection("localhost:3306");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
    public static Connection getConnection(){
        return connectionHolder.get();
    }
}