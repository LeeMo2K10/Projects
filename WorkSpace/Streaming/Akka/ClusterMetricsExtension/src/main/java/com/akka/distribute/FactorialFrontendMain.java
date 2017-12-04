package com.akka.distribute;

import java.util.concurrent.TimeUnit;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

public class FactorialFrontendMain {
	final int upToN = 200;

	public void test(String port) {
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port)
				.withFallback(ConfigFactory.parseString("akka.cluster.roles = [backend]"))
				.withFallback(ConfigFactory.load("factorial"));
		ActorSystem system = ActorSystem.create("ClusterSystem", config);

		system.log().info("Factorials will start when 2 backend members in the cluster.");
		Cluster.get(system).registerOnMemberUp(new Runnable() {
			@Override
			public void run() {
				system.actorOf(Props.create(FactorialFrontend.class, upToN, true), "factorialFrontend");
			}
		});

		Cluster.get(system).registerOnMemberRemoved(new Runnable() {
			@Override
			public void run() {
				final Runnable exit = new Runnable() {
					@Override
					public void run() {
						System.exit(0);
					}
				};
				system.registerOnTermination(exit);
				system.terminate();
				new Thread() {
					@Override
					public void run() {
						try {
							Await.ready(system.whenTerminated(), Duration.create(10, TimeUnit.SECONDS));
						} catch (Exception e) {
							System.exit(-1);
						}

					}
				}.start();
			}
		});
		// #registerOnRemoved

	}

	public static void main(String[] args) {
		FactorialFrontendMain main = new FactorialFrontendMain();
		main.test(args[0]);
	}

}
