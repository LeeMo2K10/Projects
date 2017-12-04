package com.akka.stream;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class DrainSourceToList {

	ActorSystem system = ActorSystem.create("Pipelining");
	Materializer materializer = ActorMaterializer.create(system);

	LoggingAdapter log = Logging.getLogger(system, this);

	public void drainSourceToList() {

		final Source<Integer, NotUsed> source = Source.range(1, 120);

		//Without Limit
		final CompletionStage<List<Integer>> strings = source.runWith(Sink.seq(), materializer);

		try {
			strings.toCompletableFuture().get(3, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {

			e.printStackTrace();
		}
		strings.thenAcceptAsync(f -> {
			log.info("" + f);
		});

		// With Limit
		CompletionStage<List<Integer>> strings2 = source.limit(100).runWith(Sink.seq(), materializer);

		strings2.thenAcceptAsync(f -> {
			log.info("" + f);
		});

	}

	public static void main(String[] args) {

		DrainSourceToList drain = new DrainSourceToList();

		drain.drainSourceToList();
	}
}
