import java.util.StringJoiner;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhengzebiao
 * @date 2019/12/17 23:24
 */
public class StringJoinerTest {
    public static void main(String[] args) {
        String[] names = new String[]{"Bob", "Alice", "Grace"};
        StringJoiner sj = new StringJoiner(", ","Hello ","!");
        for(String name:names){
            sj.add(name);
        }
        String namesString = String.join(", ",names);
        System.out.println(sj.toString());
        System.out.println(namesString);
        ExecutorService executor = Executors.newFixedThreadPool(10);



    }
}