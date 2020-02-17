package world.dao;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author zhengzebiao
 * @date 2020/2/14 16:01
 */
public interface StudentMarksDAO {
    /**
     * This is the method to be used to initialize
     * database resources ie. connection.
     */
    public void setDataSource(DataSource ds);

    /**
     * This is the method to be used to create
     * a record in the Student and Marks tables.
     */
    public void create(String name, Integer age, Integer marks, Integer year);

    /**
     * This is the method to be used to list down
     * all the records from the Student and Marks tables.
     */
    public List<StudentMarks> listStudents();

}