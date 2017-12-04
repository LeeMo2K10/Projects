package com.akka.stream.Source;

import java.util.Optional;

import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class UnFoldApp {

	ActorSystem system = ActorSystem.create("Pipelining");
	Materializer mat = ActorMaterializer.create(system);

	LoggingAdapter log = Logging.getLogger(system, this);

	public void cycle() {

		Source.unfold(5, f -> {

			if (f-- > 0)
				return Optional.of(Pair.apply(f, f));
			else
				return Optional.empty();

		}).runWith(Sink.foreach(i -> log.debug("" + i)), mat);
	}

	public static void main(String[] args) {

		UnFoldApp app = new UnFoldApp();
		app.cycle();
	}

}
