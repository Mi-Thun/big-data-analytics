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

public class MostVisited {
    public static class MostVisitedMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] line = value.toString().split("\\s+");
            int count = Integer.parseInt(line[1]);
            context.write(new Text(line[0]), new IntWritable(count));
        }
    }
    public static class MostVisitedReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable(Integer.MIN_VALUE);
        private Text MostVisitedPage = new Text();

        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int maxCount = Integer.MIN_VALUE;
            for (IntWritable value : values) {
                int count = value.get();
                if (count > maxCount) {
                	maxCount = count;
                }
            }
            if (maxCount > result.get()) {
            	MostVisitedPage.set(key);
                result.set(maxCount);
            }
        }
        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            context.write(MostVisitedPage, result);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job1 = Job.getInstance(conf, "Least Visited Page");
        job1.setJarByClass(MostVisited.class);
        job1.setMapperClass(MostVisitedMapper.class);
        job1.setReducerClass(MostVisitedReducer.class);
        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job1, new Path(args[0]));
        FileOutputFormat.setOutputPath(job1, new Path(args[1]));
        job1.waitForCompletion(true);
    }
}
