package com.akka.distribute;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;

public class FactorialBackendMain {

	public void test() {

		final Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + 2552)
				.withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]"))
				.withFallback(ConfigFactory.load("factorial"));

		ActorSystem system = ActorSystem.create("ClusterSystem", config);

		system.actorOf(Props.create(FactorialBackend.class), "factorialBackend");

		system.actorOf(Props.create(MetricsListener.class), "metricsListener");

	}

	public static void main(String[] args) {
		FactorialBackendMain main = new FactorialBackendMain();
		main.test();
	}
}
