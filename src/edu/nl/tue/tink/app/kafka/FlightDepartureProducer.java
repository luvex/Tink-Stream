package edu.nl.tue.tink.app.kafka;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import edu.nl.tue.tink.config.Configurations;

public class FlightDepartureProducer implements Runnable
{
	private static KafkaProducer<String, String> producer;
	private final Properties props = new Properties();
	private static String topic;
	private static int randomObjectNumber;

	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg" + "|png|mp3|mp4|zip|gz))$");

	public FlightDepartureProducer(String _topic, int _randomObjectNumber)
	{
		props.put("bootstrap.servers", Configurations.BROKER_LIST_LOCAL);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		producer = new KafkaProducer<String, String>(props);

		topic = _topic;
		randomObjectNumber = _randomObjectNumber;

	}

	public void run()
	{
		int count = 0;
		while (count++ < 1000)
		{
			
			try
			{ // ams

				ZonedDateTime zdt = ZonedDateTime.now();
				String url = "https://www.schiphol.nl/en/departures/?datetime=" + zdt.getYear() + "-"
						+ zdt.getMonthValue() + "-" + zdt.getDayOfMonth() + "%20" + zdt.getHour() + "%3A"
						+ zdt.getMinute() + "%3A" + zdt.getSecond();
				Document doc = Jsoup.connect(url).get();
				Elements newsHeadlines = doc.select("tbody");
				for (Element headline : newsHeadlines)
				{
					String a = "Amsterdam|";
					int i = 0;
					for (Element col : headline.select("tr"))
					{
						if (i == 0)
						{
							String flightNumber = "";
							int j = 0;
							for (Element col1 : col.select("th"))
							{
								if (j == 0)
								{
									String time = col1.text();
									if (time.contains("Expected"))
										a += time.split("Expected time:")[1].trim();
									else
										a += time.trim();
								}
								else if (j == 1)
								{
									flightNumber = col1.text().trim();
								}
								else
									break;
								j++;
							}
							for (Element col2 : col.select("td"))
							{
								String arrivalAirport = col2.text();
								a = a + "|" + col2.text().split(" ")[0].trim() + "|";
								break;
							}

							ProducerRecord<String, String> data = new ProducerRecord<String, String>(topic,
									a + flightNumber);
							producer.send(data);
							if (Configurations.IS_LOCAL)
								System.out.println(a + flightNumber);
						}
						else
						{
							for (Element col1 : col.select("th"))
							{
								System.out.println(a + col1.text());
								ProducerRecord<String, String> data = new ProducerRecord<String, String>(topic,
										a + col1.text());
								producer.send(data);
								break;
							}
						}
						i++;
					}
				}
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try
			{
				// dus

				String url = "https://www.dus.com/en/flights/depature";
				Document doc = Jsoup.connect(url).get();
				Elements newsHeadlines = doc.select("tr");
				for (Element headline : newsHeadlines)
				{
					int i = 0;
					String a = "Dusseldorf|";
					String time = "";
					String dest = "";
					List<String> flightNumber = new ArrayList<String>();
					if (headline.select("td").size() == 8)
					{
						for (Element col : headline.select("td"))
						{
							if (i == 1)
							{
								if (col.text().contains("canceled"))
									break;

								String[] ffs = col.text().split(" ");
								int fsize = ffs.length / 2;
								for (int j = 0; j < fsize; j++)
								{
									flightNumber.add(ffs[j * 2] + " " + ffs[j * 2 + 1]);
								}
							}
							else if (i == 2)
							{
								dest = col.text().replaceAll("aus:", "").trim();
							}
							else if (i == 3)
							{
								String xx = col.text().split(" ")[1];
								if (col.text().contains("PM"))
								{
									time = "" + (Integer.parseInt(xx.split(":")[0]) + 12) + ":" + xx.split(":")[1];
								}
								else
								{
									time = xx;
								}
							}
							i++;
						}
						if (flightNumber.size() == 0)
							continue;
						for (String flightNO : flightNumber)
						{
							ProducerRecord<String, String> data = new ProducerRecord<String, String>(topic,
									a + time + "|" + dest + "|" + flightNO);
							producer.send(data);
							if (Configurations.IS_LOCAL)
								System.out.println(a + time + "|" + dest + "|" + flightNO);
						}
					}
				}
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try
			{
				Thread.sleep(10000);
			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try
			{ // nice

				for (int i = ZonedDateTime.now().getHour(); i < 24; i++)
				{
					String url = "https://en.nice.aeroport.fr/anca/ajaxload/tpl/table_departs.tpl?flightdateDeparts="
							+ DateTime.now().toString("yyyyMMdd")
							+ "&airportDeparts=&flightNumberDeparts=&airlineDeparts=&timeperiodDeparts="
							+ ZonedDateTime.now().getHour() + "&ContentObjectID=1502&sens=D&_=1516342288256";
					Document doc = Jsoup.connect(url).get();
					Elements newsHeadlines = doc.select("tr");
					int j = 0;
					for (Element headline : newsHeadlines)
					{
						if (j++ == 0)
							continue;
						else
						{
							int k = 0;
							String arrival = "";
							String time = "";
							String flightno = "";
							String from = "Nice";
							for (Element col : headline.select("td"))
							{
								if (k == 0)
								{
									time = col.text().trim();
								}
								else if (k == 1)
								{
									arrival = col.text().trim();
								}
								else if (k == 2)
								{
									flightno = col.text().trim().substring(0, 2) + " "
											+ col.text().trim().substring(2, col.text().trim().length());
								}
								k++;
							}
							ProducerRecord<String, String> data = new ProducerRecord<String, String>(topic,
									from + "|" + time + "|" + arrival + "|" + flightno);
							producer.send(data);
							if (Configurations.IS_LOCAL)
								System.out.println(from + "|" + time + "|" + arrival + "|" + flightno);
						}
					}
				}
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
		System.out.println("end crawling");
		producer.close();
	}

	public static void main(String[] args) throws InterruptedException
	{
		if (args != null && args.length == 2)
		{
			FlightDepartureProducer tp = new FlightDepartureProducer(args[0], Integer.parseInt(args[1]));
			tp.run();
		}
		else
		{
			System.out.print("Please add the topic name and number of random instance as parameters");
			return;
		}

	}
}
