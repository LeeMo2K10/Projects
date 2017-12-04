package com.akka.distribute;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;

public class DestinationTest {

	public void test() {
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + 2554)
				.withFallback(ConfigFactory.load());
		ActorSystem system = ActorSystem.create("ActorSystem", config);

		system.actorOf(Props.create(Destination.class), "destination");

	}

	public static void main(String[] args) {
		DestinationTest test = new DestinationTest();
		test.test();
	}

}
