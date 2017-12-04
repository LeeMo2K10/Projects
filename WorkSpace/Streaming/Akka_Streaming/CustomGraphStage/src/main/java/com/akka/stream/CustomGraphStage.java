package com.akka.stream;

import java.io.Serializable;
import java.util.concurrent.CompletionStage;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Graph;
import akka.stream.Materializer;
import akka.stream.SourceShape;
import akka.stream.javadsl.Source;

public class CustomGraphStage implements Serializable {

	private static final long serialVersionUID = 1L;

	ActorSystem system = ActorSystem.create("CustomGraphStage");

	Materializer mat = ActorMaterializer.create(system);

	LoggingAdapter log = Logging.getLogger(system, this);

	private void create() {

		Graph<SourceShape<Integer>, NotUsed> source = new NumberSource();

		Source<Integer, NotUsed> mysource = Source.fromGraph(source);

		// Returns 55

		CompletionStage<Integer> result1 = mysource.take(10).runFold(0, (sum, next) -> sum + next, mat);

		result1.thenAcceptAsync(f -> {
			log.info("" + f);
		});

		// The source is reusable. This returns 5050

		CompletionStage<Integer> result2 = mysource.take(100).runFold(0, (sum, next) -> sum + next, mat);

		result2.thenAcceptAsync(f -> {
			log.info("" + f);
		});

	}

	public static void main(String[] args) {
		CustomGraphStage stage = new CustomGraphStage();
		stage.create();

	}
}