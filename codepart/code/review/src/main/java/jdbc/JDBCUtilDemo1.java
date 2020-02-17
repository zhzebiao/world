package jdbc;

import java.sql.*;

/**
 * @author zhengzebiao
 * @date 2019/12/18 15:47
 */
public class JDBCUtilDemo1 {

    /**
     * 1. 加载驱动
     */
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 2. 建立连接
     */

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/blog?serverTimezone=GMT&characterEncoding=utf-8",
                    "root", "123456");
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

    public static void main(String[] args) {
        // 测试数据库连接是否生效
        Connection connection = JDBCUtilDemo1.getConnection();
        System.out.println(connection);
        JDBCUtilDemo1.close(connection, null, null);
    }
}