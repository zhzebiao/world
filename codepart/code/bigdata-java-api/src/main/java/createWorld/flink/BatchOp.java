package createWorld.flink;

import org.apache.commons.collections.iterators.ArrayListIterator;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.accumulators.IntCounter;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapPartitionFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.LocalEnvironment;
import org.apache.flink.api.java.aggregation.Aggregations;
import org.apache.flink.api.java.io.CsvReader;
import org.apache.flink.api.java.operators.*;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.datanucleus.identity.DatastoreId;
import scala.Int;

import java.util.*;
import java.util.stream.Stream;

public class BatchOp {

    public static void main(String[] args) throws Exception {
//        useReadHdfs();
//        useRecursiveReadFile();
//        useMap();
//        useFlatMap();
//        userMapPartition();
//        useFilter();
//        useReduce();
//        useGroupBy();
//        useAggregate();
//        useJoin();
//        useRebalance();
//        useHashPartition();
//        useSortPartition();
//        useBroadcast();
        useBatchCounter();
    }

    private static class User {
        private String id;
        private String name;

        public User() {
        }

        public User(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "User{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    public static class Score {
        private int id;
        private String name;
        private int subjectId;
        private double score;

        public Score() {
        }

        public Score(int id, String name, int subjectId, double score) {
            this.id = id;
            this.name = name;
            this.subjectId = subjectId;
            this.score = score;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(int subjectId) {
            this.subjectId = subjectId;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        @Override
        public String toString() {
            return "Score{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", subjectId=" + subjectId +
                    ", score=" + score +
                    '}';
        }
    }

    public static class Subject {
        private int id;
        private String name;

        public Subject() {
        }

        public Subject(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Subject{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    private static void useBatchCounter() throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSource<String> dataSet = env.fromElements("a", "b", "c", "d");
        MapOperator<String, String> res = dataSet.map(new RichMapFunction<String, String>() {

            int sum = 0;
            IntCounter numLines = new IntCounter();

            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
                getRuntimeContext().addAccumulator("num_lines", this.numLines);
            }

            @Override
            public String map(String s) throws Exception {
                sum += 1;
                System.out.println("sum: " + sum);
                this.numLines.add(1);
                return s;
            }
        }).setParallelism(1);

        res.writeAsText("./data/batchCounter");
        JobExecutionResult batchCounter = env.execute("BatchCounter");
        Integer num = batchCounter.getAccumulatorResult("num_lines");
        System.out.println(num);

    }

    private static void useSortPartition() throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        DataSet<String> wordDataSet = env.fromCollection(
                Arrays.asList("hadoop", "hadoop", "hadoop", "hive", "hive",
                        "spark", "spark", "flink")
        );
        SortPartitionOperator<String> sortDataSet = wordDataSet.sortPartition(String::toString, Order.DESCENDING);
        sortDataSet.writeAsText("./data/sort_output/");
        env.execute("App");
    }

    private static void useHashPartition() throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        DataSet<Integer> numDataSet = env.fromCollection(
                new ArrayList<Integer>() {{
                    add(1);
                    add(1);
                    add(1);
                    add(1);
                    add(1);
                    add(1);
                    add(2);
                    add(2);
                    add(2);
                    add(2);
                    add(1);
                }}
        );
        PartitionOperator<Integer> partitionDataSet = numDataSet.partitionByHash(Object::toString);
        partitionDataSet.writeAsText("./data/partition_output");
        partitionDataSet.print();
    }

    private static void useRebalance() throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Long> numDataSet = env.generateSequence(0, 100);
        numDataSet.filter(num -> {
            return num > 8;
        })
                .rebalance()
                .map(new RichMapFunction<Long, Tuple2<Integer, Long>>() {
                    @Override
                    public Tuple2<Integer, Long> map(Long aLong) throws Exception {
                        return new Tuple2<Integer, Long>(getRuntimeContext().getIndexOfThisSubtask(), aLong);
                    }
                }).print();
    }

    private static void useJoin() throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<Subject> subjectDataSet = env.readCsvFile("D:\\个人空间\\黑马大数据课件\\33--Flink基础\\23--Flink基础\\flink基础\\资料\\测试数据源\\subject.csv").pojoType(Subject.class, "id", "name");
        DataSet<Score> scoreDataSet = env.readCsvFile("D:\\个人空间\\黑马大数据课件\\33--Flink基础\\23--Flink基础\\flink基础\\资料\\测试数据源\\score.csv").pojoType(Score.class, "id", "name", "subjectId", "score");
        JoinOperator.DefaultJoin<Score, Subject> scoreSubjectDefaultJoin = scoreDataSet.join(subjectDataSet).where("subjectId").equalTo("id");
        scoreSubjectDefaultJoin.print();


    }

    private static void useAggregate() throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSource<Tuple2<String, Integer>> wordDataSet = env.fromCollection(
                new ArrayList<Tuple2<String, Integer>>() {{
                    add(new Tuple2<String, Integer>("java", 1));
                    add(new Tuple2<String, Integer>("java", 2));
                    add(new Tuple2<String, Integer>("scala", 1));
                }}
        );
        AggregateOperator<Tuple2<String, Integer>> aggregate = wordDataSet.groupBy(0).aggregate(Aggregations.SUM, 1);
        aggregate.print();

    }

    private static void useGroupBy() throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSource<Tuple2<String, Integer>> wordDataSet = env.fromCollection(
                Arrays.asList(new Tuple2<String, Integer>("scala", 1), new Tuple2<String, Integer>("java", 2), new Tuple2<String, Integer>("java", 1))
        );
        UnsortedGrouping<Tuple2<String, Integer>> tuple2UnsortedGrouping = wordDataSet.groupBy(tuple2 -> {
            return tuple2.f0;
        });
        ReduceOperator<Tuple2<String, Integer>> reduce = tuple2UnsortedGrouping.reduce(
                ((tuple2, t1) -> {
                    return new Tuple2<String, Integer>(t1.f0, t1.f1 + tuple2.f1);
                })
        );
        reduce.print();
    }

    private static void useReduce() throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSource<Tuple2> wordDataSet = env.fromCollection(
                Arrays.asList(new Tuple2<String, Integer>("java", 1), new Tuple2<String, Integer>("java", 1), new Tuple2<String, Integer>("java", 1))

        );
        ReduceOperator<Tuple2> reduce = wordDataSet.reduce(
                (Tuple2 w1, Tuple2 w2) -> {
                    return new Tuple2(w1.f0, (Integer) w1.f1 + (Integer) w2.f1);
                }
        );

        reduce.print();
    }

    private static void useFilter() throws Exception {
        ExecutionEnvironment environment = ExecutionEnvironment.getExecutionEnvironment();
        DataSource<String> wordDataSet = environment.fromCollection(
                Arrays.asList("1,张三", "2,李四", "3,王五", "4,赵六")
        );
        FilterOperator<String> filter = wordDataSet.filter(x -> {
            return x.startsWith("1");
        });
        filter.print();
    }

    private static void useFlatMap() throws Exception {
        ExecutionEnvironment environment = ExecutionEnvironment.getExecutionEnvironment();
        DataSource<String> userDataSet = environment.fromCollection(
                Arrays.asList(
                        "张三,中国,江西省,南昌市",
                        "李四,中国,河北省,石家庄市",
                        "Tom,America,NewYork,Manhattan"
                )
        );
        FlatMapOperator<String, Tuple2<String, String>> resultDataSet = userDataSet.flatMap((FlatMapFunction<String, Tuple2<String, String>>) (String s, Collector<Tuple2<String, String>> collector) -> {
            String[] fieldArray = s.split(",");
            List<Tuple2<String, String>> resultList = new ArrayList<>();
            collector.collect(new Tuple2<>(fieldArray[0], fieldArray[1]));
            collector.collect(new Tuple2<>(fieldArray[0], fieldArray[1] + fieldArray[2]));
            collector.collect(new Tuple2<>(fieldArray[0], fieldArray[1] + fieldArray[2] + fieldArray[3]));
        });
        resultDataSet.print();
    }

    // TODO: 泛型的类型抹除问题
    private static void userMapPartition() throws Exception {
        ExecutionEnvironment environment = ExecutionEnvironment.getExecutionEnvironment();
        DataSource<String> userDataSet = environment.fromCollection(
                new ArrayList<String>() {{
                    add("1,张三");
                    add("2,李四");
                    add("3,王五");
                    add("4,赵六");
                }}
        );
        MapPartitionOperator<String, User> resultDataSet = userDataSet.mapPartition(
//                (iterable, collector) -> {
//                    iterable.forEach(x -> {
//                        String[] fieldArray = x.split(",");
//                        collector.collect(new User(fieldArray[0], fieldArray[1]));
//                    });
//                }

                new MapPartitionFunction<String, User>() {
                    @Override
                    public void mapPartition(Iterable<String> iterable, Collector<User> collector) throws Exception {
                        iterable.forEach(x -> {
                            String[] fieldArray = x.split(",");
                            collector.collect(new User(fieldArray[0], fieldArray[1]));
                        });
                    }
                }
        );
        resultDataSet.print();
    }

    private static void useMap() throws Exception {
        ExecutionEnvironment environment = ExecutionEnvironment.getExecutionEnvironment();
        DataSource<String> textDataSet = environment.fromCollection(
                Arrays.asList("1,张三", "2,李四", "3,王五", "4,赵六")
        );

        MapOperator<String, User> userDataSet = textDataSet.map(text -> {
            String[] fieldArray = text.split(",");
            return new User(fieldArray[0], fieldArray[1]);
        });

        userDataSet.print();
    }

    private static void useRecursiveReadFile() throws Exception {
        ExecutionEnvironment environment = ExecutionEnvironment.getExecutionEnvironment();
        Configuration parameters = new Configuration();
        parameters.setBoolean("recursive.file.enumeration", true);
        DataSource<String> result = environment.readTextFile("hdfs://node01:8020/output/").withParameters(parameters);
        result.print();
    }

    private static void useReadHdfs() throws Exception {
        ExecutionEnvironment environment = ExecutionEnvironment.getExecutionEnvironment();
        DataSource<String> datas = environment.readTextFile("hdfs://node01:8020/input/partition.csv");
        datas.print();
    }

    private static void useBroadcast() throws Exception {
        ExecutionEnvironment environment = ExecutionEnvironment.getExecutionEnvironment();
        // 1. 分别创建两个数据集
        DataSet<Tuple2<Integer, String>> studentDataSet = environment.fromCollection(
                new ArrayList<Tuple2<Integer, String>>() {{
                    add(new Tuple2<>(1, "张三"));
                    add(new Tuple2<>(2, "李四"));
                    add(new Tuple2<>(3, "王五"));
                }}
        );
        DataSet<Tuple3<Integer, String, Integer>> scoreDataSet = environment.fromCollection(
                new ArrayList<Tuple3<Integer, String, Integer>>() {{
                    add(new Tuple3<>(1, "语文", 50));
                    add(new Tuple3<>(2, "数学", 70));
                    add(new Tuple3<>(3, "英文", 86));
                }}
        );

        MapOperator<Tuple3<Integer, String, Integer>, Tuple3<String, String, Integer>> resultDataSet = scoreDataSet.map(new RichMapFunction<Tuple3<Integer, String, Integer>, Tuple3<String, String, Integer>>() {

            List<Tuple2<Integer, String>> bcStudentList = null;

            @Override
            public void open(Configuration parameters) throws Exception {
//                super.open(parameters);
                bcStudentList = getRuntimeContext().getBroadcastVariable("bc_student");
            }

            @Override
            public Tuple3<String, String, Integer> map(Tuple3<Integer, String, Integer> value) throws Exception {
                Integer studentId = value.f0;
                Tuple2<Integer, String> first = bcStudentList.stream().filter(x -> x.f0.equals(studentId)).findFirst().get();
                return new Tuple3<>(first.f1, value.f1, value.f2);
            }
        }).withBroadcastSet(studentDataSet, "bc_student");

        resultDataSet.print();
    }


}
