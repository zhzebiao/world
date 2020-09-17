package createWorld.flink;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.CommonClientConfigs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class StreamOp {
    private static StreamExecutionEnvironment sEnv = StreamExecutionEnvironment.getExecutionEnvironment();

    public static void main(String[] args) throws Exception {

//        streamFromCollectionSource();
//        streamFromSocketSource();
//        customSource();
//        streamFromKafkaSource();
//        useKeyBy();
//        useConnect();
        useSplitAndSelect();
    }

    private static void useKafkaDataSink() throws Exception {
        DataStreamSource<Tuple4<Integer, String, String, String>> source = sEnv.addSource(new MySqlSource());
        SingleOutputStreamOperator<String> map = source.map(new MapFunction<Tuple4<Integer, String, String, String>, String>() {
            @Override
            public String map(Tuple4<Integer, String, String, String> tuple4) throws Exception {
                return tuple4.f0 + tuple4.f1 + tuple4.f2 + tuple4.f3;
            }
        });
        Properties properties = new Properties();
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "node01:9092,node02:9092,node03:9092");
        FlinkKafkaProducer010<String> sink = new FlinkKafkaProducer010<>("topicName", new SimpleStringSchema(), properties);
        map.addSink(sink);
        sEnv.execute();
    }

    private static void useSplitAndSelect() throws Exception {
        StreamExecutionEnvironment sEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Integer> eleDateStream = sEnv.fromElements(1, 2, 3, 4, 5, 6);
        SplitStream<Integer> split = eleDateStream.split(new OutputSelector<Integer>() {
            @Override
            public Iterable<String> select(Integer value) {
                List<String> output = new ArrayList<>();
                if (value % 2 == 0) {
                    output.add("even");
                } else {
                    output.add("odd");
                }
                return output;
            }
        });
        DataStream<Integer> even = split.select("even");
        DataStream<Integer> odd = split.select("odd");
        split.select("even", "odd");
        odd.print();
        sEnv.execute();
    }

    private static void useConnect() throws Exception {
        StreamExecutionEnvironment sEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<Long> longDataStreamSource = sEnv.addSource(new MyLongSource());
        DataStreamSource<String> stringDataStreamSource = sEnv.addSource(new MyStringSource());
        ConnectedStreams<Long, String> connect = longDataStreamSource.connect(stringDataStreamSource);
        SingleOutputStreamOperator<Object> map = connect.map(new CoMapFunction<Long, String, Object>() {
            @Override
            public Object map1(Long aLong) throws Exception {
                return aLong;
            }

            @Override
            public Object map2(String s) throws Exception {
                return s;
            }
        });
        map.print().setParallelism(1);
        sEnv.execute();
    }

    private static void useKeyBy() throws Exception {
        StreamExecutionEnvironment sEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        sEnv.setParallelism(3);
        DataStream<String> socketTextStream = sEnv.socketTextStream("node01", 9999, "\n", 10);
        SingleOutputStreamOperator<Tuple2<String, Integer>> text = socketTextStream.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {
                for (String part : s.split("\\s")) {
                    collector.collect(part);
                }
            }
        }).map(new MapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String s) throws Exception {
                return new Tuple2<>(s, 1);
            }
        }).keyBy(0).sum(1);
        text.print();
        sEnv.execute();
    }

    private static void streamFromMysqlSource() {
        StreamExecutionEnvironment sEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        sEnv.addSource(new MySqlSource());
    }

    private static void streamFromKafkaSource() throws Exception {
        String kafkaCluster = "node01:9092,node02:9092,node02:9092";
        String kafkaTopic = "kafkaTopic";
        StreamExecutionEnvironment sEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties properties = new Properties();
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaCluster);
        FlinkKafkaConsumer010<String> kafkaStream = new FlinkKafkaConsumer010<>(kafkaTopic, new SimpleStringSchema(), properties);
        DataStream<String> textDataStream = sEnv.addSource(kafkaStream, TypeInformation.of(String.class));
        textDataStream.print();
        sEnv.execute();
    }

    private static void customSource() throws Exception {
        StreamExecutionEnvironment sEnv = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> orderDataStream = sEnv.addSource(new SourceFunction<String>() {
            @Override
            public void run(SourceContext<String> sourceContext) throws Exception {
                for (int i = 0; i < 1000; i++) {
                    // 随机生成订单ID（UUID）
                    String id = UUID.randomUUID().toString();
                    // 收集数据
                    sourceContext.collect(id);
                    // 每隔1秒生成一个订单
                    TimeUnit.SECONDS.sleep(1);
                }
            }

            @Override
            public void cancel() {

            }
        }, TypeInformation.of(String.class));
        orderDataStream.print();
        // 执行程序
        sEnv.execute();

    }

    private static void streamFromSocketSource() throws Exception {
        StreamExecutionEnvironment sEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> socketDataStream = sEnv.socketTextStream("node01", 9999, '\n', 10);
        DataStream<String> mapDataStream = socketDataStream.flatMap((FlatMapFunction<String, String>) (s, collector) -> {
            String[] split = s.split("\\s");
            for (String string : split) {
                collector.collect(string);
            }
        });
        mapDataStream.print();
        sEnv.execute("WordCount_Stream");
    }

    private static void streamFromCollectionSource() throws Exception {
        StreamExecutionEnvironment senv = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> textDataStream = senv.readTextFile("./data/sort_output");
        textDataStream.print();
        senv.execute();
    }


    static class MySqlSource implements SourceFunction<Tuple4<Integer, String, String, String>> {

        @Override
        public void run(SourceContext<Tuple4<Integer, String, String, String>> sourceContext) throws Exception {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc://mysql:///test", "root", "123456");
            String sql = "select id , username , password , name from user";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Integer id = resultSet.getInt("id");
                String userName = resultSet.getString("username");
                String password = resultSet.getString("passowrd");
                String name = resultSet.getString("name");
                sourceContext.collect(new Tuple4<>(id, userName, password, name));
            }
        }

        @Override
        public void cancel() {

        }
    }

    static class MyLongSource implements SourceFunction<Long> {

        boolean isRunning = true;
        Long count = 1L;

        @Override
        public void run(SourceContext<Long> sourceContext) throws Exception {
            while (isRunning) {
                sourceContext.collect(count);
                count += 1;
                TimeUnit.SECONDS.sleep(1);
            }
        }

        @Override
        public void cancel() {
            isRunning = false;
        }
    }

    static class MyStringSource implements SourceFunction<String> {
        boolean isRunning = true;
        Long count = 1L;

        @Override
        public void run(SourceContext<String> sourceContext) throws Exception {
            while (isRunning) {
                sourceContext.collect("str_" + count);
                count += 1;
                TimeUnit.SECONDS.sleep(10);
            }
        }

        @Override
        public void cancel() {
            isRunning = false;
        }
    }

}
