package chapter_2_io;

import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/**
 * @author zhzeb
 * @date 2020/5/24 10:22
 */
public class TextFileTest {

    public static class Employee {
        private String name;
        private double salary;
        private int year;
        private int month;
        private int day;

        public Employee(String name, double salary, int year, int month, int day) {
            this.name = name;
            this.salary = salary;
            this.year = year;
            this.month = month;
            this.day = day;
        }
    }

    public static void main(String[] args) {
        Employee[] staff = new Employee[3];
        staff[0] = new Employee("Carl Cracker", 75000, 1987, 12, 15);
        staff[1] = new Employee("Harry Hacker", 50000, 1989, 10, 1);
        staff[2] = new Employee("Tony Tester", 40000, 1990, 3, 15);

    }
}