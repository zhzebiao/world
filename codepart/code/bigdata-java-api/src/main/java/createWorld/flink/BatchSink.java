package createWorld.flink;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.runtime.executiongraph.Execution;
import org.apache.hadoop.io.Writable;

import java.util.HashMap;

public class BatchSink {
    public static void main(String[] args) throws Exception {
        batchSinkCollection();
//        batchSinkFile();
    }

    private static void batchSinkFile() throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<HashMap<Integer, String>> dataSet1 = env.fromElements(
                new HashMap<Integer, String>() {{
                    put(1, "spark");
                    put(2, "flink");
                }}
        );
        env.setParallelism(1);
//        dataSet1.writeAsText("./data/data1/aa", FileSystem.WriteMode.OVERWRITE);
        dataSet1.writeAsText("hdfs://node01:8020/data/data1/aa", FileSystem.WriteMode.OVERWRITE);
        env.execute();
    }

    private static void batchSinkCollection() throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Tuple3<Integer, String, Double>> stu = env.fromElements(
                new Tuple3<Integer, String, Double>(19, "zhangsan", 178.8),
                new Tuple3<Integer, String, Double>(17, "lisi", 168.8),
                new Tuple3<Integer, String, Double>(18, "wangwu", 184.8),
                new Tuple3<Integer, String, Double>(21, "zhaoliu", 164.8)
        );
        stu.print();
        stu.printToErr();
        System.out.println(stu.collect());
        env.execute();
    }
}
