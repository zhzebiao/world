package createWorld.flink;

import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.time.Time;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class StreamWaterMark {
    private static StreamExecutionEnvironment sEnv = StreamExecutionEnvironment.getExecutionEnvironment();

    public static void main(String[] args) throws Exception {
        // 设置时间为`EventTime`
        sEnv.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        DataStreamSource<Tuple4<String, Integer, Long, Long>> orderDataStream = sEnv.addSource(new RichSourceFunction<Tuple4<String, Integer, Long, Long>>() {
            boolean isRunning = true;

            @Override
            public void run(SourceContext<Tuple4<String, Integer, Long, Long>> sourceContext) throws Exception {
                while (isRunning) {
                    // - 随机生成订单ID（UUID）
                    // - 随机生成用户ID（0-2）
                    // - 随机生成订单金额（0-100）
                    // - 时间戳为当前系统时间
                    // - 每隔1秒生成一个订单
                    Tuple4<String, Integer, Long, Long> order = new Tuple4<>(UUID.randomUUID().toString(), new Random().nextInt(3),
                            new Random().nextLong(), System.currentTimeMillis());
                    sourceContext.collect(order);
                    TimeUnit.SECONDS.sleep(1);
                }
            }

            @Override
            public void cancel() {
                isRunning = false;
            }
        });



        SingleOutputStreamOperator<Tuple4<String, Integer, Long, Long>> watermarks =
                orderDataStream.assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarks<Tuple4<String, Integer, Long, Long>>() {
            Long currentTimestamp = 0L;
            Long delayTime = 2000L;

            @Nullable
            @Override
            public Watermark getCurrentWatermark() {

                Watermark watermark = new Watermark(currentTimestamp - delayTime);
                System.out.println(String.format("当前水印时间%s,当前事件时间%s,当前系统时间%s", watermark, currentTimestamp, System.currentTimeMillis()));
                return watermark;
            }

            @Override
            public long extractTimestamp(Tuple4<String, Integer, Long, Long> order, long previousElementTimestamp) {
                Long timestamp = order.f3;
                currentTimestamp = Math.max(currentTimestamp, timestamp);
                return currentTimestamp;
            }
        });
        watermarks.keyBy(x -> x.f1).timeWindow(Time.seconds(5)).reduce((order1, order2) -> {
            return new Tuple4<>(order1.f0, order1.f1, order1.f2 + order2.f2, 0L);
        }).print();
        sEnv.execute();
    }
}
