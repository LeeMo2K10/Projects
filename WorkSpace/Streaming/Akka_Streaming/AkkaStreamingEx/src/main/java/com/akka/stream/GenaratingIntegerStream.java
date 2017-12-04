package com.akka.stream;

import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.concurrent.CompletionStage;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.IOResult;
import akka.stream.Materializer;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Source;
import akka.util.ByteString;

public class GenaratingIntegerStream {

	final ActorSystem system = ActorSystem.create("QuickStart");
	Materializer materializer = ActorMaterializer.create(system);

	LoggingAdapter log = Logging.getLogger(system, this);

	public void getStream() {

		final Source<Integer, NotUsed> source = Source.range(1, 100);

		source.runForeach(i -> System.out.println(i), materializer);

		final Source<BigInteger, NotUsed> factorials = source.scan(BigInteger.ONE,
				(acc, next) -> acc.multiply(BigInteger.valueOf(next)));
		final CompletionStage<IOResult> result = factorials.map(num -> ByteString.fromString(num.toString() + "\n"))
				.runWith(FileIO.toPath(Paths.get("src/main/resources/Factorial.txt")), materializer);

		result.thenAcceptAsync(f -> {
			log.info("" + f);
		});

	}

	public static void main(String[] args) {
		GenaratingIntegerStream stream = new GenaratingIntegerStream();
		stream.getStream();
	}
}
