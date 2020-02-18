package Main;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.setting.Setting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author zhengzebiao
 * @date 2019/12/26 14:22
 */
public class Main {

    private static final Log LOG = LogFactory.get();
    public static void main(String[] args) {
        Setting applicationSetting = new Setting("application.setting");
        String driverClassName = applicationSetting.get("driverClassName").trim();
        String url = applicationSetting.get("url").trim();
        String username = applicationSetting.get("username").trim();
        String password = applicationSetting.get("password").trim();

        Connection conn = null;
        PreparedStatement ptmt = null;
        try {
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            LOG.error("==========init fail===========");
        }
        String sql = applicationSetting.get("sql").trim();
        try {
            ptmt = conn.prepareStatement(sql);
            ptmt.setInt(1, 1);
            ptmt.setString(2, "topic");
            ptmt.setString(3, "day");
            ptmt.setString(4, "district");
            ptmt.setLong(5, 1L);
            ptmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                ptmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}