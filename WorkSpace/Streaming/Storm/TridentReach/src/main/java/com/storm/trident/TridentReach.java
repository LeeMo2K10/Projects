package com.storm.trident;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.LocalDRPC;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.operation.builtin.MapGet;
import storm.trident.operation.builtin.Sum;

public class TridentReach {
	static Logger logger = Logger.getLogger(TridentReach.class);
	public static Map<String, List<String>> TWEETERS_DB = new HashMap<String, List<String>>() {

		private static final long serialVersionUID = 1L;

		{
			put("foo.com/blog/1", Arrays.asList("sally", "bob", "tim", "george", "nathan"));
			put("engineering.twitter.com/blog/5", Arrays.asList("adam", "david", "sally", "nathan"));
			put("tech.backtype.com/blog/123", Arrays.asList("tim", "mike", "john"));
		}
	};

	public static Map<String, List<String>> FOLLOWERS_DB = new HashMap<String, List<String>>() {

		private static final long serialVersionUID = 1L;

		{
			put("sally", Arrays.asList("bob", "tim", "alice", "adam", "jim", "chris", "jai", "chinna"));
			put("bob", Arrays.asList("sally", "nathan", "jim", "mary", "david", "vivian"));
			put("tim", Arrays.asList("alex"));
			put("nathan", Arrays.asList("sally", "bob", "adam", "harry", "chris", "vivian", "emily", "jordan"));
			put("adam", Arrays.asList("david", "carissa"));
			put("mike", Arrays.asList("john", "bob"));
			put("john", Arrays.asList("alice", "nathan", "jim", "mike", "bob"));
		}
	};

	public static StormTopology buildTopology(LocalDRPC drpc) {
		TridentTopology topology = new TridentTopology();
		TridentState urlToTweeters = topology.newStaticState(new StaticSingleKeyMapState.Factory(TWEETERS_DB));
		TridentState tweetersToFollowers = topology.newStaticState(new StaticSingleKeyMapState.Factory(FOLLOWERS_DB));

		topology.newDRPCStream("reach", drpc)
				.stateQuery(urlToTweeters, new Fields("args"), new MapGet(), new Fields("tweeters"))
				.each(new Fields("tweeters"), new ExpandList(), new Fields("tweeter")).shuffle()
				.stateQuery(tweetersToFollowers, new Fields("tweeter"), new MapGet(), new Fields("followers"))
				.each(new Fields("followers"), new ExpandList(), new Fields("follower")).groupBy(new Fields("follower"))
				.aggregate(new One(), new Fields("one")).aggregate(new Fields("one"), new Sum(), new Fields("reach"));
		return topology.build();
	}

	public static void main(String[] args) throws Exception {
		LocalDRPC drpc = new LocalDRPC();

		Config conf = new Config();
		LocalCluster cluster = new LocalCluster();

		cluster.submitTopology("reach", conf, buildTopology(drpc));

		Thread.sleep(1000);

		logger.info("REACH: " + drpc.execute("reach", "tech.backtype.com/blog/123"));
		logger.info("REACH: " + drpc.execute("reach", "foo.com/blog/1"));
		logger.info("REACH: " + drpc.execute("reach", "engineering.twitter.com/blog/5"));

	}
}