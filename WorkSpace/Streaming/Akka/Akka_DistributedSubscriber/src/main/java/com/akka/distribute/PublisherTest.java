package com.akka.distribute;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class PublisherTest {

	public void test() {
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + 2553)
				.withFallback(ConfigFactory.load());
		ActorSystem system = ActorSystem.create("ActorSystem", config);
		ActorRef ref = system.actorOf(Props.create(Publisher.class), "publisher");

		for (int i = 1; i <= 1000000; i++) {

			ref.tell("hello" + i, null);
		}

	}

	public static void main(String[] args) {
		PublisherTest test = new PublisherTest();
		test.test();
	}

}
