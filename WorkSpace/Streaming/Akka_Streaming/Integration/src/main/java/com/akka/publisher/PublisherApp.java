package com.akka.publisher;

import java.io.Serializable;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class PublisherApp implements Serializable {

	private static final long serialVersionUID = 1L;

	ActorSystem system = ActorSystem.create("PublisherApp");

	Materializer mat = ActorMaterializer.create(system);

	LoggingAdapter log = Logging.getLogger(system, this);

	void publsh() {

		Source<Job, ActorRef> source = Source.actorPublisher(JobManager.props());

		ActorRef ref = source.map(job -> job.payload.toUpperCase()).map(elem -> {
			System.out.println(elem);
			return elem;
		}).to(Sink.ignore()).run(mat);

		ref.tell(new Job("a"), ActorRef.noSender());
		ref.tell(new Job("b"), ActorRef.noSender());

		ref.tell(new Job("c"), ActorRef.noSender());

	}

	public static void main(String[] args) {

		PublisherApp app = new PublisherApp();
		app.publsh();

	}

}
