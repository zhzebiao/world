package createworld.springboot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



class SpringbootApplicationTests {

//    @Autowired
//    DataSource dataSource;
//
//    @Test
//    void contextLoads() throws SQLException {
//        Connection connection = dataSource.getConnection();
//        PreparedStatement ptmt = connection.prepareStatement("select * from es");
//        ResultSet resultSet = ptmt.executeQuery();
//        while (resultSet.next()){
//            System.out.println(resultSet.getInt("id"));
//        }
//    }



}
