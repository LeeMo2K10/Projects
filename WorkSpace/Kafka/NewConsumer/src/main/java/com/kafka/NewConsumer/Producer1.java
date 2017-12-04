package com.kafka.NewConsumer;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class Producer1 {
	public static void main(String[] args) {
		Properties props = new Properties();

		props.put("metadata.broker.list", "192.168.1.54:9092");
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("request.required.acks", "1");

		ProducerConfig config = new ProducerConfig(props);
		Producer<String, String> kafkaProducer = new Producer<String, String>(config);

		for (int dataitem = 0; dataitem < 50; ++dataitem) {
			KeyedMessage<String, String> data = new KeyedMessage<String, String>("test-topic2", "BIZ +" + dataitem);
			kafkaProducer.send(data);
		}

		System.out.println("Data sending completed!");

		kafkaProducer.close();
	}
}