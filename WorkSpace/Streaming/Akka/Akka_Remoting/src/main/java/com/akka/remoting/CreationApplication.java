package com.akka.remoting;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import scala.concurrent.duration.Duration;

public class CreationApplication {

	public static void startRemoteWorkerSystem() {
		ActorSystem.create("CalculatorWorkerSystem", ConfigFactory.load(("calculator")));
		System.out.println("Started CalculatorWorkerSystem");
	}

	public static void startRemoteCreationSystem() {
		final ActorSystem system = ActorSystem.create("CreationSystem", ConfigFactory.load("remotecreation"));
		final ActorRef actor = system.actorOf(Props.create(CreationActor.class), "creationActor");

		System.out.println("Started CreationSystem");
		final Random r = new Random();
		system.scheduler().schedule(Duration.create(1, TimeUnit.SECONDS), Duration.create(1, TimeUnit.SECONDS),
				new Runnable() {
					@Override
					public void run() {
						if (r.nextInt(100) % 2 == 0) {
							actor.tell(new Multiply(r.nextInt(100), r.nextInt(100)), null);
						} else {
							actor.tell(new Divide(r.nextInt(10000), r.nextInt(99) + 1), null);
						}
					}
				}, system.dispatcher());
	}

	public static void main(String[] args) {

		if (args.length == 0 || args[0].equals("CalculatorWorker"))
			startRemoteWorkerSystem();
		if (args.length == 0 || args[0].equals("Creation"))
			startRemoteCreationSystem();

	}
}
