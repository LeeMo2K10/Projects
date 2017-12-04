package com.kafka.NewConsumer;

import kafka.api.FetchRequest;

import kafka.api.FetchRequestBuilder;
import kafka.javaapi.FetchResponse;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.message.ByteBufferMessageSet;
import kafka.message.MessageAndOffset;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class SimpleConsumerDemo {
	static Logger logger = Logger.getLogger(SimpleConsumerDemo.class);

	private static void printMessages(ByteBufferMessageSet messageSet) throws UnsupportedEncodingException {
		for (MessageAndOffset messageAndOffset : messageSet) {
			ByteBuffer payload = messageAndOffset.message().payload();
			byte[] bytes = new byte[payload.limit()];
			payload.get(bytes);
			logger.debug(new String(bytes, "UTF-8"));
		}
	}

	private static void generateData() {
		Producer producer2 = new Producer(KafkaProperties.topic2, false);
		producer2.start();
		Producer producer3 = new Producer(KafkaProperties.topic3, false);
		producer3.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		generateData();

		SimpleConsumer simpleConsumer = new SimpleConsumer(KafkaProperties.kafkaServerURL,
				KafkaProperties.kafkaServerPort, KafkaProperties.connectionTimeOut,
				KafkaProperties.kafkaProducerBufferSize, KafkaProperties.clientId);
		logger.info("Testing single fetch");
		FetchRequest req = new FetchRequestBuilder().clientId(KafkaProperties.clientId)
				.addFetch(KafkaProperties.topic2, 0, 0L, 100).build();
		FetchResponse fetchResponse = simpleConsumer.fetch(req);
		printMessages(fetchResponse.messageSet(KafkaProperties.topic2, 0));
		logger.info("Testing single multi-fetch");
		Map<String, List<Integer>> topicMap = new HashMap<String, List<Integer>>();
		topicMap.put(KafkaProperties.topic2, Collections.singletonList(0));
		topicMap.put(KafkaProperties.topic3, Collections.singletonList(0));
		req = new FetchRequestBuilder().clientId(KafkaProperties.clientId).addFetch(KafkaProperties.topic2, 0, 0L, 100)
				.addFetch(KafkaProperties.topic3, 0, 0L, 100).build();
		fetchResponse = simpleConsumer.fetch(req);
		int fetchReq = 0;
		for (Map.Entry<String, List<Integer>> entry : topicMap.entrySet()) {
			String topic = entry.getKey();
			for (Integer offset : entry.getValue()) {
				logger.debug("Response from fetch request no: " + ++fetchReq);
				printMessages(fetchResponse.messageSet(topic, offset));
			}
		}
	}
}