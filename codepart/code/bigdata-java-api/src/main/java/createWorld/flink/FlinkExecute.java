package createWorld.flink;

import org.apache.flink.api.java.CollectionEnvironment;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.LocalEnvironment;

import java.util.Arrays;
import java.util.Date;

public class FlinkExecute {
    public static void main(String[] args) throws Exception {
//        executeLocalEnvironment();
        executeCollectionEnvironment();
    }

    private static void executeRemoteEnvironment() {
        ExecutionEnvironment.createRemoteEnvironment("node01",8081,"");
    }

    private static void executeCollectionEnvironment() throws Exception {
        CollectionEnvironment env = ExecutionEnvironment.createCollectionsEnvironment();
        long startTime = new Date().getTime();

        DataSet<String> dataSet = env.fromCollection(
                Arrays.asList("1", "2")
        );
        dataSet.print();
        long endTime = new Date().getTime();
        System.out.println(endTime - startTime);

    }

    private static void executeLocalEnvironment() throws Exception {
        LocalEnvironment env = ExecutionEnvironment.createLocalEnvironment();
        long startTime = new Date().getTime();

        DataSet<String> dataSet = env.fromCollection(
                Arrays.asList("1", "2")
        );
        dataSet.print();
        long endTime = new Date().getTime();
        System.out.println(endTime - startTime);
    }
}
