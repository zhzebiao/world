package jdbc;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author zhengzebiao
 * @date 2019/12/18 17:55
 */
public class JDBCUtilDruid {

    private static Properties properties = new Properties();
    private static DataSource dataSource = null;

    // 加载配置文件,构建连接池
    static {
        InputStream inputStream = JDBCUtilDruid.class.getResourceAsStream("/druid.properties");
        try {
            properties.load(inputStream);
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close(Connection conn, PreparedStatement ptmt, ResultSet rs) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // 打印错误信息
                conn = null;
            }
        }
        if (ptmt != null) {
            try {
                ptmt.close();
            } catch (SQLException e) {
                // 打印错误信息
                ptmt = null;
            }
        }
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                // 打印错误信息
                rs = null;
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        String insertSql = "INSERT INTO es(type,topic,date,place,number) values(?,?,?,?,?)";
        Connection conn = JDBCUtilDruid.getConnection();
        PreparedStatement ptmt = conn.prepareStatement(insertSql);
        ptmt.setString(1, "read");
        ptmt.setString(2, "tb_face");
        ptmt.setString(3, "tb_face");
        ptmt.setString(4, "tb_face");
        ptmt.setLong(5, 1234);
        ptmt.executeUpdate();

        String selectSql = "select * from es";
        ResultSet resultSet = ptmt.executeQuery(selectSql);
        while (resultSet.next()) {
            System.out.println(resultSet.getInt("id"));
        }

        JDBCUtilDruid.close(conn, ptmt, null);
    }
}