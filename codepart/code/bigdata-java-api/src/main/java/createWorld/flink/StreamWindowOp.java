package createWorld.flink;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class StreamWindowOp {

    private static StreamExecutionEnvironment sEnv = StreamExecutionEnvironment.getExecutionEnvironment();

    public static void main(String[] args) throws Exception {
//        streamTumblingTimeWindow();
        windowApply();
    }

    private static void windowApply() throws Exception {
        DataStreamSource<String> socket = sEnv.socketTextStream("node01", 9999, "\n", 10);
        KeyedStream<Tuple2<String, Integer>, String> keyedStream = socket.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception {
                Arrays.stream(s.split("\\s")).map(part -> new Tuple2<String, Integer>(part, 1)).forEach(collector::collect);
            }
        }).keyBy(x -> x.f0);
        WindowedStream<Tuple2<String, Integer>, String, TimeWindow> windowedStream = keyedStream.timeWindow(Time.seconds(3));
        SingleOutputStreamOperator<Tuple2<String, Integer>> resultDataStream = windowedStream.apply(new WindowFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, String, TimeWindow>() {
            @Override
            public void apply(String key, TimeWindow timeWindow, Iterable<Tuple2<String, Integer>> input, Collector<Tuple2<String, Integer>> collector) throws Exception {
                Optional<Tuple2<String, Integer>> reduce = StreamSupport.stream(input.spliterator(), false).reduce((tupleA, tupleB) -> {
                    return new Tuple2<>(tupleA.f0, tupleA.f1 + tupleB.f1);
                });
                collector.collect(reduce.get());
            }
        });
        resultDataStream.print();
        sEnv.execute();
    }

    private static void streamSlidingCountWindow() throws Exception {
        DataStreamSource<String> socketSource = sEnv.socketTextStream("node01", 9999, "\n", 10);
        SingleOutputStreamOperator<Tuple2<String, Integer>> map = socketSource.map(new MapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String s) throws Exception {
                return new Tuple2<>(s, 1);
            }
        });
        KeyedStream<Tuple2<String, Integer>, String> keyByStream = map.keyBy(x -> x.f0);
        SingleOutputStreamOperator<Tuple2<String, Integer>> sum = keyByStream.countWindow(5, 3).sum(1);
        sum.print();
        sEnv.execute();
    }

    private static void streamTumblingCountWindow() throws Exception {
        DataStreamSource<String> socketSource = sEnv.socketTextStream("node01", 9999, "\n", 10);
        SingleOutputStreamOperator<Tuple2<String, Integer>> map = socketSource.map(new MapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String s) throws Exception {
                return new Tuple2<>(s, 1);
            }
        });
        KeyedStream<Tuple2<String, Integer>, String> keyByStream = map.keyBy(x -> x.f0);
        SingleOutputStreamOperator<Tuple2<String, Integer>> sum = keyByStream.countWindow(5).sum(1);
        sum.print();
        sEnv.execute();
    }

    private static void streamSlidingTimeWindow() throws Exception {
        DataStreamSource<String> socketSource = sEnv.socketTextStream("node01", 9999, "\n", 10);
        SingleOutputStreamOperator<Tuple2<String, Integer>> map = socketSource.map(new MapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String s) throws Exception {
                return new Tuple2<>(s, 1);
            }
        });
        KeyedStream<Tuple2<String, Integer>, String> keyByStream = map.keyBy(x -> x.f0);
        SingleOutputStreamOperator<Tuple2<String, Integer>> sum = keyByStream.timeWindow(Time.seconds(10), Time.seconds(2)).sum(1);
        keyByStream.timeWindow(Time.seconds(10)).reduce((t1, t2) -> {
            return new Tuple2<String, Integer>(t1.f0, t1.f1 + t2.f1);
        });
        sum.print();
        sEnv.execute();
    }

    private static void streamTumblingTimeWindow() throws Exception {
        DataStreamSource<String> socketSource = sEnv.socketTextStream("node01", 9999, "\n", 10);
        SingleOutputStreamOperator<Tuple2<String, Integer>> map = socketSource.map(new MapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String s) throws Exception {
                return new Tuple2<>(s, 1);
            }
        });
        KeyedStream<Tuple2<String, Integer>, String> keyByStream = map.keyBy(x -> x.f0);
        SingleOutputStreamOperator<Tuple2<String, Integer>> sum = keyByStream.timeWindow(Time.seconds(5)).sum(1);
        sum.print();
        sEnv.execute();
    }
}
