package edu.nl.tue.tink.config;

public class Configurations {

	public static String BROKER_LIST = "172.16.130.12:9092,172.16.130.44:9092,172.16.130.4:9092";
	public static String BROKER_LIST_LOCAL = "127.0.0.1:9092";
	public static boolean IS_LOCAL = true;
	
	public static String ZK_HOSTS="172.16.130.12:2181,172.16.130.44:2181,172.16.130.4:2181";
	public static String ZK_HOSTS_LOCAL="127.0.0.1:2181";
	
	public static boolean IF_LOGGING = true;
}
