import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.DoubleWritable;


public class SumMapper extends Mapper<LongWritable, Text, Text, DoubleWritable> {

    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
    String[] fields = value.toString().split(",");
    for (String field : fields) {
      double num = Double.parseDouble(field);
      context.write(new Text("sum"), new DoubleWritable(num));
    }
  }
}

