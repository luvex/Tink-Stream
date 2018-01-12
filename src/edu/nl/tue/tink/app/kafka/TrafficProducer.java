package edu.nl.tue.tink.app.kafka;

import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import edu.nl.tue.tink.config.Configurations;

public class TrafficProducer
{

	private static KafkaProducer<String, String> producer;
	private final Properties props = new Properties();
	private static String topic;
	private static int randomObjectNumber;

	public TrafficProducer(String _topic, int _randomObjectNumber)
	{
		props.put("bootstrap.servers", Configurations.BROKER_LIST_LOCAL);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		producer = new KafkaProducer<String,String>(props);

		topic = _topic;
		randomObjectNumber = _randomObjectNumber;

	}

	public void run()
	{

		Random random = new Random();
		String messageStr = null;
		int i = 500000;
		while ((i--) != 0)
		{
			messageStr = random.nextInt(randomObjectNumber) + "";
			ProducerRecord<String, String> data = new ProducerRecord<String, String>(topic, messageStr);
			producer.send(data);
			if (Configurations.IS_LOCAL)
				System.out.println("sent: " + data);
		}
		producer.close();
	}

	public static void main(String[] args) throws InterruptedException
	{
		if (args != null && args.length == 2)
		{
			TrafficProducer tp = new TrafficProducer(args[0], Integer.parseInt(args[1]));
			tp.run();
		}
		else
		{
			System.out.print("Please add the topic name and number of random instance as parameters");
			return;
		}

	}

}
