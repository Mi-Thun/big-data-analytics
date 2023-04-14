import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class PairFrequency {
  public static class SumMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
    public void map(LongWritable key, Text value, Context context)
        throws IOException, InterruptedException {
      String[] fields = value.toString().split(" ");
      Set<String> uniqueFields = new HashSet<>(Arrays.asList(fields));
      String[] uniqueFieldsArray = uniqueFields.toArray(new String[uniqueFields.size()]);
      Arrays.sort(uniqueFieldsArray);
      int j = 0;
      String f = "";
      for (int i = 0; i < uniqueFieldsArray.length; i++) {
        for (j = i + 1; j < uniqueFieldsArray.length; j++) {
          if (uniqueFieldsArray[i].equals(uniqueFieldsArray[j]) || f.equals(uniqueFieldsArray[j])) {
            continue;
          }
          f = uniqueFieldsArray[j];
          String a = uniqueFieldsArray[i].toString().trim() + " " + uniqueFieldsArray[j].toString().trim();
          context.write(new Text(a), new IntWritable(1));
        }
      }
    }
  }
  public static class SumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    public void reduce(Text key, Iterable<IntWritable> values, Context context)
        throws IOException, InterruptedException {
      int sum = 0;
      for (IntWritable val : values) {
        sum += val.get();
      }
      context.write(key, new IntWritable(sum));
    }
  }

  public static void main(String[] args) throws Exception {
    Job job = Job.getInstance();
    job.setJarByClass(PairFrequency.class);
    job.setJobName("Pair Frequency Count");
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    job.setMapperClass(SumMapper.class);
    job.setReducerClass(SumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
