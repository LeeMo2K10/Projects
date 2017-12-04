package com.storm.kafka.mysql;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;

public class App {
	public void test() throws AlreadyAliveException, InvalidTopologyException {
		ZkHosts zkHosts = new ZkHosts("192.168.1.54:2181");

		String topic = "test-topic2";
		String consumer_group_id = "id10";

		SpoutConfig kafkaConfig = new SpoutConfig(zkHosts, topic, "", consumer_group_id);

		kafkaConfig.forceFromStart = true;
		kafkaConfig.scheme = new SchemeAsMultiScheme(new StringScheme());

		KafkaSpout kafkaSpout = new KafkaSpout(kafkaConfig);

		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("KafkaSpout", kafkaSpout, 12);
		builder.setBolt("MySqlBolt", new MySqlDumpBolt(), 12).globalGrouping("KafkaSpout");

		Config config = new Config();
		config.setDebug(true);
		config.setNumWorkers(6);
		config.setNumAckers(6);
		config.setMaxSpoutPending(100);
		config.setMessageTimeoutSecs(20);
		// LocalCluster cluster = new LocalCluster();
		StormSubmitter.submitTopology("MySqlDemoTopology", config, builder.createTopology());
		Utils.sleep(10000);

	}

	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
		App app = new App();
		app.test();
	}
}