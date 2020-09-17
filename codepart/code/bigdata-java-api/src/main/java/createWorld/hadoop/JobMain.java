package createWorld.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class JobMain extends Configured implements Tool {
    @Override
    public int run(String[] strings) throws Exception {


        Configuration configuration = super.getConf();
        configuration.set("mapreduce.framework.name", "local");
        configuration.set("yarn.resourcemanager.hostname", "local");

        Job job = Job.getInstance(configuration, JobMain.class.getSimpleName());
        // 打包到集群上运行的时候，必须要添加以下配置，指定程序的main函数
        job.setJarByClass(JobMain.class);
        // 第一步：读取输入文件，解析成key,value对
        job.setInputFormatClass(TextInputFormat.class);
        TextInputFormat.addInputPath(job, new Path("hdfs://node01:8020/wordcount"));

        // 第二步：设置mapper类
        job.setMapperClass(WordCountMapper.class);
        // 设置我们mapper阶段完成之后的输出类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);
        // 第三步，第四步，第五步，第六步，省略
        // 第七步：设置reduce类
        job.setReducerClass(WordCountReducer.class);
        //设置reduce阶段完成之后的输出类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        //第八步：设置输出类以及输出路径
        job.setOutputFormatClass(TextOutputFormat.class);
        TextOutputFormat.setOutputPath(job, new Path("hdfs://node01:8020/wordcount_out"));
        boolean b = job.waitForCompletion(true);
        return b ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        Tool tool = new JobMain();
        int run = ToolRunner.run(configuration, tool, args);
        System.exit(run);
    }
}
