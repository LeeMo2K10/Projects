package com.akka.stream;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.CompletionStage;

import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Graph;
import akka.stream.Materializer;
import akka.stream.SinkShape;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class CustomLinearProcessing implements Serializable {

	private static final long serialVersionUID = 1L;

	ActorSystem system = ActorSystem.create("CustomGraphStage");

	Materializer mat = ActorMaterializer.create(system);

	LoggingAdapter log = Logging.getLogger(system, this);

	private void process() {

		Graph<SinkShape<Integer>, CompletionStage<String>> sink = Sink.fold("", (acc, n) -> acc + n.toString());

		CompletionStage<String> resultFuture = Source.from(Arrays.asList(1, 2, 3, 4, 5, 6))
				.via(new Filter<Integer>((n) -> n % 2 == 0)).via(new Duplicator<Integer>())
				.via(new Map<Integer, Integer>((n) -> n / 2)).runWith(sink, mat);
		resultFuture.thenAcceptAsync(f -> {
			log.info("" + f);
		});
	}

	public static void main(String[] args) {

		CustomLinearProcessing process = new CustomLinearProcessing();
		process.process();
	}
}
