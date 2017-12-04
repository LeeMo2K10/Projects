package com.akka.externalserveices;

import java.util.Arrays;

import java.util.concurrent.Executor;

import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.ActorMaterializerSettings;
import akka.stream.javadsl.Source;

public class UsingMapAsync {

	ActorSystem system = ActorSystem.create("External");
	final Executor blockingEc = system.dispatchers().lookup("blocking-dispatcher");
	final SometimesSlowService service = new SometimesSlowService(blockingEc);

	final ActorMaterializer mat = ActorMaterializer
			.create(ActorMaterializerSettings.create(system).withInputBuffer(4, 4), system);
	LoggingAdapter log = Logging.getLogger(system, this);

	public void slow() {

		Source.from(Arrays.asList("a", "B", "C", "D", "e", "F", "g", "H", "i", "J")).map(elem -> {
			System.out.println("before: " + elem);
			return elem;
		}).mapAsync(4, service::convert).runForeach(elem -> System.out.println("after: " + elem), mat);
	}

	public static void main(String[] args) {

		UsingMapAsync asyn = new UsingMapAsync();
		asyn.slow();
	}
}