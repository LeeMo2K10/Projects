package com.akka.stream.Source;

import java.util.Arrays;
import java.util.Iterator;

import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.function.Creator;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class CycleApp {

	ActorSystem system = ActorSystem.create("Pipelining");
	Materializer mat = ActorMaterializer.create(system);

	LoggingAdapter log = Logging.getLogger(system, this);

	public void cycle() {
		Creator<Iterator<Integer>> creator = new Creator<Iterator<Integer>>() {
			private static final long serialVersionUID = 1L;

			public Iterator<Integer> create() throws Exception {
				Iterable<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
				return input.iterator();
			}
		};
		Source.cycle(creator).runWith(Sink.foreach(i -> log.debug("" + i)), mat);
	}

	public static void main(String[] args) {

		CycleApp app = new CycleApp();
		app.cycle();
	}

}