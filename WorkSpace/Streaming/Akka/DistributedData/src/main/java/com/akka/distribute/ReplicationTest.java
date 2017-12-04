package com.akka.distribute;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class ReplicationTest {

	public void test() {

		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + 2551)
		//		.withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]"))
				.withFallback(ConfigFactory.load());

		ActorSystem system = ActorSystem.create("ClusterSystem", config);

		ActorRef ref = system.actorOf(Props.create(DataBot.class), "Listener");

	}

	public static void main(String[] args) {
		ReplicationTest test = new ReplicationTest();
		test.test();
	}

}
