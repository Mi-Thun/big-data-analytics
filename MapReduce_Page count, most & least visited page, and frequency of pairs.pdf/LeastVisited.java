import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class LeastVisited {
    public static class LeastVisitedMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] line = value.toString().split("\\s+");
            int count = Integer.parseInt(line[1]);
            context.write(new Text(line[0]), new IntWritable(count));
        }
    }
    public static class LeastVisitedReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable(Integer.MAX_VALUE);
        private Text leastVisitedPage = new Text();

        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int minCount = Integer.MAX_VALUE;
            for (IntWritable value : values) {
                int count = value.get();
                if (count < minCount) {
                    minCount = count;
                }
            }
            if (minCount < result.get()) {
                leastVisitedPage.set(key);
                result.set(minCount);
            }
        }
        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            context.write(leastVisitedPage, result);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job1 = Job.getInstance(conf, "Least Visited Page");
        job1.setJarByClass(LeastVisited.class);
        job1.setMapperClass(LeastVisitedMapper.class);
        job1.setReducerClass(LeastVisitedReducer.class);
        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job1, new Path(args[0]));
        FileOutputFormat.setOutputPath(job1, new Path(args[1]));
        job1.waitForCompletion(true);
    }
}
