package com.akka.integration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.akka.publisher.Msg;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class SubScriberApp implements Serializable {

	private static final long serialVersionUID = 1L;

	ActorSystem system = ActorSystem.create("SubScriberApp");

	Materializer mat = ActorMaterializer.create(system);

	LoggingAdapter log = Logging.getLogger(system, this);

	public void subscribe() {

		int N = 117;

		List<Integer> data = new ArrayList<>(N);

		for (int i = 0; i < N; i++) {
			data.add(i);
		}

		Source.from(data).map(i -> WorkerPoolProtocol.msg(i, system.actorOf(Props.create(ReplyTo.class))))
				.runWith(Sink.<Msg>actorSubscriber(WorkerPool.props()), mat);

	}

	public static void main(String[] args) {

		SubScriberApp app = new SubScriberApp();
		app.subscribe();
	}
}
