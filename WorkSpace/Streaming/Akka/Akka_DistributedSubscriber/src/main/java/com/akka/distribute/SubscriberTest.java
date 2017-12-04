package com.akka.distribute;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;

public class SubscriberTest {

	public void test() {
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + 2554)
				.withFallback(ConfigFactory.load());
		ActorSystem system = ActorSystem.create("ActorSystem", config);

		system.actorOf(Props.create(Subscriber.class), "subscriber1");
		// system.actorOf(Props.create(Subscriber.class), "subscriber2");
		// system.actorOf(Props.create(Subscriber.class), "subscriber3");

	}

	public static void main(String[] args) {
		SubscriberTest test = new SubscriberTest();
		test.test();
	}

}