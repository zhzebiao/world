import java.util.Scanner;

public class TestY {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int count = scanner.nextInt();
        while ((count--) != 0) {
            int a = scanner.nextInt();
            int b = scanner.nextInt();
            int p = scanner.nextInt();
            int q = scanner.nextInt();
            int time = 1;
            while (p < b - a) {
                p = p * q;
                time++;
            }
            System.out.println(time);
        }
    }
}