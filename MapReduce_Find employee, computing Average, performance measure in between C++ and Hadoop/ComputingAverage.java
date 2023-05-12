import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.*;

public class ComputingAverage {
    public static class Map extends Mapper<LongWritable, Text, IntWritable, DoubleWritable> {
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            StringTokenizer tokenizer = new StringTokenizer(line);
            while (tokenizer.hasMoreTokens()) {
                double val = Double.parseDouble(tokenizer.nextToken());
                context.write(new IntWritable(1), new DoubleWritable(val));
            }
        }
    }

    public static class Reduce extends Reducer<IntWritable, DoubleWritable, NullWritable, DoubleWritable> {
        public void reduce(IntWritable key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
            double sum = 0;
            int count = 0;
            for (DoubleWritable value : values) {
                sum += value.get();
                count++;
            }
            double average = sum / count;
            context.write(NullWritable.get(), new DoubleWritable(average));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "average");
        job.setJarByClass(ComputingAverage.class);
        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(DoubleWritable.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
