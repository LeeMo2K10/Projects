package com.akka.Networking;

import java.util.ArrayList;
import java.util.List;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;
import akka.cluster.Cluster;

public class SimpleClusterApp {

	public static void startup(String[] ports) {
		for (String port : ports) {
			// Override the configuration of the port
			Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port)
					.withFallback(ConfigFactory.load());

			// Create an Akka system
			ActorSystem system = ActorSystem.create("ClusterSystem", config);

			// Create an actor that handles cluster domain events
			system.actorOf(Props.create(SimpleClusterListener.class), "clusterListener");

		}
	}

	public static void main(String[] args) {

		SimpleClusterApp test = new SimpleClusterApp();

		if (args.length == 0)
			startup(new String[] { "2551", "2552", "2553" });
		else
			startup(args);

	}

}
