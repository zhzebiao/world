package world.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import world.tutorialspoint.Student;

import java.util.List;

/**
 * @author zhengzebiao
 * @date 2020/2/14 15:34
 */
public class MainApp {
    public static void main(String[] args) {
        ApplicationContext context =
                new ClassPathXmlApplicationContext("Jdbc.xml");
        StudentMarksDAO studentMarksJDBCTemplate =
                (StudentMarksDAO) context.getBean("studentMarksJDBCTemplate");
        System.out.println("------Records creation--------");
        studentMarksJDBCTemplate.create("Zara", 11, 99, 2010);
        studentMarksJDBCTemplate.create("Nuha", 20, 97, 2010);
        studentMarksJDBCTemplate.create("Ayan", 25, 100, 2011);
        System.out.println("------Listing all the records--------");
        List<StudentMarks> studentMarks = studentMarksJDBCTemplate.listStudents();
        for (StudentMarks record : studentMarks) {
            System.out.print("ID : " + record.getId());
            System.out.print(", Name : " + record.getName());
            System.out.print(", Marks : " + record.getMarks());
            System.out.print(", Year : " + record.getYear());
            System.out.println(", Age : " + record.getAge());
        }
    }
}