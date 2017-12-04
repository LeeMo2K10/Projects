package com.biz.KafkaStorm;

import java.util.Arrays;

import org.apache.log4j.Logger;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.StormTopology;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

public class KafkaSpoutTopologyTest {
	static Logger logger = Logger.getLogger(KafkaSpoutTopologyTest.class);
	private BrokerHosts brokerHosts;

	public KafkaSpoutTopologyTest(String kafkaZookeeper) {
		brokerHosts = new ZkHosts(kafkaZookeeper);
	}

	public StormTopology buildTopology() {
		SpoutConfig config = new SpoutConfig(brokerHosts, "test", "", "storm");
		config.scheme = new SchemeAsMultiScheme(new StringScheme());
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("words", new KafkaSpout(config), 10);
		builder.setBolt("print", new PrinterBolt()).shuffleGrouping("words");
		return builder.createTopology();
	}

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
		String kafkazk = "localhost:2181";
		KafkaSpoutTopologyTest test = new KafkaSpoutTopologyTest(kafkazk);
		Config conf = new Config();
		conf.put(conf.TOPOLOGY_TRIDENT_BATCH_EMIT_INTERVAL_MILLIS, 2000);
		StormTopology stormTopology = test.buildTopology();
		if (args != null && args.length > 1) {
			String name = "testStorm";
			String ip = "localhost";
			conf.setDebug(true);
			conf.setNumWorkers(10);
			conf.setMaxTaskParallelism(5);
			conf.put(Config.NIMBUS_HOST, ip);
			conf.put(Config.NIMBUS_THRIFT_PORT, 6627);
			conf.put(Config.STORM_ZOOKEEPER_PORT, 2181);
			conf.put(Config.STORM_ZOOKEEPER_SERVERS, Arrays.asList(ip));
			StormSubmitter.submitTopology(name, conf, stormTopology);
			logger.info("Data Consumed");
		} else {
			conf.setNumWorkers(10);
			conf.setMaxTaskParallelism(5);
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("kafka", conf, stormTopology);
		}
	}

}
