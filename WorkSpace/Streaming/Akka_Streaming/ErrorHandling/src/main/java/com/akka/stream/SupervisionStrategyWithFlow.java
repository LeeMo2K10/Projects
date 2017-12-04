package com.akka.stream;

import java.util.Arrays;
import java.util.concurrent.CompletionStage;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.function.Function;
import akka.stream.ActorAttributes;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.Supervision;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class SupervisionStrategyWithFlow {

	ActorSystem system = ActorSystem.create("ErrorHandling");

	LoggingAdapter log = Logging.getLogger(system, this);

	final Function<Throwable, Supervision.Directive> decider = exc -> {
		if (exc instanceof ArithmeticException)
			return Supervision.resume();
		else
			return Supervision.stop();
	};

	final Materializer mat = ActorMaterializer.create(system);

	void handle() {

		Flow<Integer, Integer, NotUsed> flow = Flow.of(Integer.class).filter(elem -> 100 / elem < 50)
				.map(elem -> 100 / (5 - elem)).withAttributes(ActorAttributes.withSupervisionStrategy(decider));

		final Source<Integer, NotUsed> source = Source.from(Arrays.asList(0, 1, 2, 3, 4, 5)).via(flow);

		final Sink<Integer, CompletionStage<Integer>> fold = Sink.fold(0, (acc, elem) -> (acc + elem));

		final CompletionStage<Integer> result = source.runWith(fold, mat);
		result.thenAcceptAsync(f -> {
			log.info("" + f);
		});

	}

	public static void main(String[] args) {

		SupervisionStrategyWithFlow su = new SupervisionStrategyWithFlow();
		su.handle();
	}
}