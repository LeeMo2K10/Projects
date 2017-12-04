package com.akka.stream;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Pair;
import akka.japi.function.Function;
import akka.japi.function.Function2;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class RecipeReduceByKeyTest {

	ActorSystem system = ActorSystem.create("Pipelining");
	Materializer materializer = ActorMaterializer.create(system);

	LoggingAdapter log = Logging.getLogger(system, this);

	private void reduceBy() {

		final Source<String, NotUsed> words = Source.from(Arrays.asList("hello", "world", "and", "hello", "akka"));

		final int MAXIMUM_DISTINCT_WORDS = 1000;

		// Normal ReduceByKey

		final Source<Pair<String, Integer>, NotUsed> counts = words.groupBy(MAXIMUM_DISTINCT_WORDS, i -> i)
				.map(i -> new Pair<>(i, 1))
				.reduce((left, right) -> new Pair<>(left.first(), left.second() + right.second())).mergeSubstreams();

		CompletionStage<List<Pair<String, Integer>>> result = counts.grouped(10).runWith(Sink.head(), materializer);

		result.thenAcceptAsync(f -> {
			log.info("" + f);
		});

		// generalized version Of reduceByKey

		Source<Pair<String, Integer>, NotUsed> counts2 = words
				.via(reduceByKey(MAXIMUM_DISTINCT_WORDS, word -> word, word -> 1, (left, right) -> left + right));

		CompletionStage<List<Pair<String, Integer>>> result2 = counts2.grouped(10).runWith(Sink.head(), materializer);
		result2.thenAcceptAsync(f -> {
			log.info("" + f);
		});

	}

	
	//New Way
	private <In, K, Out> Flow<In, Pair<K, Out>, NotUsed> reduceByKey(

			int maxGroupSize, Function<In, K> groupBy, Function<In, Out> map,

			Function2<Out, Out, Out> reduce) {

		return Flow.<In>create().groupBy(maxGroupSize, groupBy).map(i -> new Pair<>(groupBy.apply(i), map.apply(i)))
				.reduce((left, right) -> new Pair<>(left.first(), reduce.apply(left.second(), right.second())))
				.mergeSubstreams();

	}

	public static void main(String[] args) {

		RecipeReduceByKeyTest test = new RecipeReduceByKeyTest();
		test.reduceBy();

	}

}
