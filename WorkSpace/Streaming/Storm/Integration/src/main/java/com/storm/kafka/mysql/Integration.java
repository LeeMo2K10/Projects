package com.storm.kafka.mysql;

import java.util.Properties;
import java.util.UUID;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;
import storm.kafka.bolt.KafkaBolt;
import storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import storm.kafka.bolt.selector.DefaultTopicSelector;
import storm.kafka.bolt.selector.KafkaTopicSelector;
import storm.trident.testing.FixedBatchSpout;

public class Integration {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void test() {
		BrokerHosts hosts = new ZkHosts("localhost:2181");
		SpoutConfig spoutConfig = new SpoutConfig(hosts, "stormTest", "/" + "stormTest", UUID.randomUUID().toString());
		spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
		KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);
		TopologyBuilder builder = new TopologyBuilder();
		Fields fields = new Fields("key", "message");
		FixedBatchSpout spout = new FixedBatchSpout(fields, 4, new Values("storm", "1"), new Values("trident", "1"),
				new Values("needs", "1"), new Values("javadoc", "1"));
		spout.setCycle(true);
		builder.setSpout("spout", kafkaSpout, 5);
		KafkaBolt bolt = new KafkaBolt().withTopicSelector(new DefaultTopicSelector("test"))
				.withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper());
		builder.setBolt("forwardToKafka", bolt, 8).shuffleGrouping("spout");

		Config conf = new Config();
		// set producer properties.
		Properties props = new Properties();
		props.put("metadata.broker.list", "localhost:9092");
		props.put("request.required.acks", "1");
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		conf.put(KafkaBolt.KAFKA_BROKER_PROPERTIES, props);
		LocalCluster local = new LocalCluster();
		local.submitTopology("kafkaboltTest", conf, builder.createTopology());

	}
}