package com.akka.stream;

import java.io.Serializable;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Source;

public class AkkaBuffers implements Serializable {

	private static final long serialVersionUID = 1L;
	ActorSystem system = ActorSystem.create("InternalBuffers");
	Materializer mat = ActorMaterializer.create(system);
	LoggingAdapter log = Logging.getLogger(system, this);

	private void getBuffer() {

		Source<Job, NotUsed> source = Source.empty();

		final Source<Job, NotUsed> jobs = source;

		jobs.buffer(1000, OverflowStrategy.backpressure());

	}

	public static void main(String[] args) {

		AkkaBuffers buf = new AkkaBuffers();
		buf.getBuffer();
	}
}

class Job {
}