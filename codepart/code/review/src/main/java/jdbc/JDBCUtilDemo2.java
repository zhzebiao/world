package jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.Properties;

/**
 * @author zhengzebiao
 * @date 2019/12/18 16:06
 */
public class JDBCUtilDemo2 {
    private static Properties prop = new Properties();

    // 加载驱动
    static {
        try {
            InputStream inputStream = JDBCUtilDemo2.class.getResourceAsStream("/db.properties");
            prop.load(inputStream);
            Class.forName(prop.getProperty("driver"));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 2. 建立连接
     */
    public static Connection getConnection() {


        try {
            return DriverManager.getConnection(prop.getProperty("url"),prop.getProperty("username"),prop.getProperty("password"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭连接
     */
    public static void close(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                // 进行异常处理，输出异常信息
            } finally {
                resultSet = null;
            }
        }
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                // 进行异常处理，输出异常信息
            } finally {
                preparedStatement = null;
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // 进行异常处理，输出异常信息
            } finally {
                connection = null;
            }
        }

    }

    public static void main(String[] args) throws IOException {
        // 测试数据库连接是否生效
        Connection connection = JDBCUtilDemo2.getConnection();
        System.out.println(connection);
        JDBCUtilDemo2.close(connection, null, null);

    }
}