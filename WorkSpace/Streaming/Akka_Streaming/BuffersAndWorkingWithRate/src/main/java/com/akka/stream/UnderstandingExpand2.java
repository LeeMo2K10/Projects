package com.akka.stream;

import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class UnderstandingExpand2 implements Serializable {

	private static final long serialVersionUID = 1L;
	ActorSystem system = ActorSystem.create("UnderstandingConflate");
	Materializer mat = ActorMaterializer.create(system);
	LoggingAdapter log = Logging.getLogger(system, this);

	final Random r = new Random();

	private void calculate() throws InterruptedException, ExecutionException, TimeoutException {

		final Flow<Double, Pair<Double, Integer>, NotUsed> driftFlow = Flow.of(Double.class)
				.expand(d -> Stream.iterate(0, i -> i + 1).map(i -> new Pair<>(d, i)).iterator());

		final CompletionStage<List<Pair<Double, Integer>>> result = Source.repeat(0).map(i -> r.nextGaussian())
				.via(driftFlow).grouped(10).runWith(Sink.head(), mat);

		result.toCompletableFuture().get(1, TimeUnit.SECONDS);

		result.thenAcceptAsync(f -> {

			log.info("" + f);
		});
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {

		UnderstandingExpand2 con = new UnderstandingExpand2();
		con.calculate();
	}
}
