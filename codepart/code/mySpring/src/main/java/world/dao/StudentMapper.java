package world.dao;

import org.springframework.jdbc.core.RowMapper;
import world.tutorialspoint.Student;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author zhengzebiao
 * @date 2020/2/14 15:25
 */
public class StudentMapper implements RowMapper<Student> {

    @Override
    public Student mapRow(ResultSet resultSet, int i) throws SQLException {

        Student student = new Student();
        student.setAge(resultSet.getInt("age"));
        student.setName(resultSet.getString("name"));
        student.setId(resultSet.getInt("id"));
        return student;
    }
}