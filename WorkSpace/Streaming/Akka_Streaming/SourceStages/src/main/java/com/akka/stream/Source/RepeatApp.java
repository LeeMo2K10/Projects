package com.akka.stream.Source;

import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class RepeatApp {

	ActorSystem system = ActorSystem.create("Pipelining");
	Materializer mat = ActorMaterializer.create(system);

	LoggingAdapter log = Logging.getLogger(system, this);

	public void repeat() {

		Source.repeat(1).runWith(Sink.foreach(i -> log.debug("" + i)), mat);
	}

	public static void main(String[] args) {

		RepeatApp app = new RepeatApp();
		app.repeat();
	}

}
