package com.kafka.NewConsumer;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;

public class Consumer {
	static Logger logger = Logger.getLogger(Consumer.class);

	public void consumer() {
		Properties props = new Properties();
		props.put("bootstrap.servers", "192.168.1.13:9092");
		props.put("group.id", "consumer-group");
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("inter.broker.protocol.version", "0.9.0.X");
		

		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
		consumer.subscribe(Arrays.asList("testpage"));
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(100);
			for (ConsumerRecord<String, String> record : records)
				logger.debug("offset details: " + " Offset : " + record.offset() + " Key : " + record.key()
						+ " Value : " + record.value());
		}
	}

	public static void main(String[] args) {
		Consumer consumer = new Consumer();
		consumer.consumer();
	}
}