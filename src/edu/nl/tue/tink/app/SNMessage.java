package edu.nl.tue.tink.app;

import java.util.Properties;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

public class SNMessage
{

	public SNMessage()
	{
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception
	{
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		Properties properties = new Properties();
		properties.setProperty("bootstrap.servers", "localhost:9092");
		properties.setProperty("group.id", "test");

		FlinkKafkaConsumer010<String> arrConsumer = new FlinkKafkaConsumer010<>(java.util.regex.Pattern.compile("arr"),
				new SimpleStringSchema(), properties);
		FlinkKafkaConsumer010<String> depConsumer = new FlinkKafkaConsumer010<>(java.util.regex.Pattern.compile("dep"),
				new SimpleStringSchema(), properties);

		DataStream<String> arrstream = env.addSource(arrConsumer);

		DataStream<String> depstream = env.addSource(depConsumer);

		DataStream<String> joinedstream = arrstream.join(depstream).where(new Selector()).equalTo(new Selector())
				.window(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5))).apply(new myJoinFunction())
				;

		
		joinedstream.print();
		
		env.execute("SN message");
	}

	public static class Selector implements KeySelector<String, String>
	{

		@Override
		public String getKey(String arg0) throws Exception
		{
			// TODO Auto-generated method stub
			try
			{
			return arg0.split("\\|")[3];
			}
			catch(Exception e)
			{
				
			}
			return "";
		}

	}

	public static class myJoinFunction implements JoinFunction<String, String, String>
	{

		@Override
		public String join(String arg0, String arg1) throws Exception
		{
			// TODO Auto-generated method stub
			// System.out.println(arg0 + "|" + arg1);

			return arg1.split("\\|")[0] + "|" + arg1.split("\\|")[1] + "|" + arg1.split("\\|")[2] + "|"
					+ arg0.split("\\|")[1] + "|" + arg1.split("\\|")[3];
		}

	}


}
