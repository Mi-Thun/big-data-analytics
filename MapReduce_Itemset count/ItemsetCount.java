import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class SalesEmployeeFinder {

  // Mapper class to extract department and employee name
  public static class SalesEmployeeMapper extends Mapper<LongWritable, Text, Text, Text> {

    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
      String[] fields = value.toString().split(",");
	      if (fields[3].equals("Salary")!=true)
	      {
	      String department = fields[1];
	      String employeeName = fields[0];
	      String position = fields[2];
	      String salary=fields[3];
	      double sal=Double.parseDouble(salary);
	      if (sal>=60000 && sal<=90000) {
	        context.write(new Text("Result"), new Text(employeeName+' ' +department + ' ' + position ));
	      }
	      }
	    }
	  }

  // Reducer class to collect all employee names who work in Sales department
  public static class SalesEmployeeReducer extends Reducer<Text, Text, Text, Text> {

    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
      StringBuilder sb = new StringBuilder();
      for (Text val : values) {
        sb.append(val.toString()).append("\n");
      }
      context.write(key, new Text(sb.toString()));
    }
  }

  // Main method to run the job
  public static void main(String[] args) throws Exception {
    Job job = Job.getInstance();
    job.setJarByClass(SalesEmployeeFinder.class);
    job.setJobName("Find Sales Employees");
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    job.setMapperClass(SalesEmployeeMapper.class);
    job.setReducerClass(SalesEmployeeReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}