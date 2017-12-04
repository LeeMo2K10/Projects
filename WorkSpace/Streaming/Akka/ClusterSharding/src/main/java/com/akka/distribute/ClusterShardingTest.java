package com.akka.distribute;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.sharding.ClusterSharding;
import akka.cluster.sharding.ClusterShardingSettings;
import akka.cluster.sharding.ShardRegion;
import akka.japi.Option;

public class ClusterShardingTest {

	public void test() {
		Option<String> roleOption = Option.none();
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + 2552)
				.withFallback(ConfigFactory.load());
		ActorSystem system = ActorSystem.create("ActorSystem", config);

		ClusterShardingSettings settings = ClusterShardingSettings.create(system);

		ShardRegion.MessageExtractor messageExtractor = new ShardRegion.MessageExtractor() {

			@Override
			public String shardId(Object message) {
				int numberOfShards = 100;
				if (message instanceof EntityEnvelope) {
					long id = ((EntityEnvelope) message).id;
					return String.valueOf(id % numberOfShards);
				} else if (message instanceof Get) {
					long id = ((Get) message).counterId;
					return String.valueOf(id % numberOfShards);
				} else {
					return null;
				}

			}

			@Override
			public Object entityMessage(Object message) {
				if (message instanceof EntityEnvelope)
					return ((EntityEnvelope) message).payload;
				else
					return message;
			}

			@Override
			public String entityId(Object message) {
				if (message instanceof EntityEnvelope)
					return String.valueOf(((EntityEnvelope) message).id);
				else if (message instanceof Get)
					return String.valueOf(((Get) message).counterId);
				else
					return null;
			}
		};
		ActorRef startedCounterRegion = ClusterSharding.get(system).start("Counter", Props.create(Counter.class),
				settings, messageExtractor);

		// ActorRef counterRegion =
		// ClusterSharding.get(system).shardRegion("Counter");
		startedCounterRegion.tell(new Get(123), null);
		startedCounterRegion.tell(new EntityEnvelope(123, CounterOp.INCREMENT), null);
		startedCounterRegion.tell(new Get(123), null);

		ClusterSharding.get(system).start("SupervisedCounter", Props.create(CounterSupervisor.class), settings,
				messageExtractor);

	}

	public static void main(String[] args) {
		ClusterShardingTest test = new ClusterShardingTest();
		test.test();
	}

}
