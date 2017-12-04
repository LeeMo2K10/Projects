package com.akka.integration;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import akka.stream.actor.AbstractActorSubscriber;
import akka.stream.actor.ActorSubscriberMessage;
import akka.stream.actor.MaxInFlightRequestStrategy;
import akka.stream.actor.RequestStrategy;

public class WorkerPool extends AbstractActorSubscriber {

	public static Props props() {
		return Props.create(WorkerPool.class);
	}

	final int MAX_QUEUE_SIZE = 10;
	final Map<Integer, ActorRef> queue = new HashMap<>();
	final Router router;

	@Override
	public RequestStrategy requestStrategy() {
		return new MaxInFlightRequestStrategy(MAX_QUEUE_SIZE) {

			@Override
			public int inFlightInternally() {
				return queue.size();
			}
		};
	}

	public WorkerPool() {
		final List<Routee> routees = new ArrayList<>();

		for (int i = 0; i < 3; i++)
			routees.add(new ActorRefRoutee(context().actorOf(Props.create(Worker.class))));

		router = new Router(new RoundRobinRoutingLogic(), routees);

		receive(ReceiveBuilder.match(ActorSubscriberMessage.OnNext.class, on -> on.element() instanceof Msg, onNext -> {
			Msg msg = (Msg) onNext.element();
			queue.put(msg.id, msg.replyTo);

			if (queue.size() > MAX_QUEUE_SIZE)
				throw new RuntimeException("queued too many: " + queue.size());
			router.route(WorkerPoolProtocol.work(msg.id), self());
		}).match(Reply.class, reply -> {
			int id = reply.id;
			queue.get(id).tell(WorkerPoolProtocol.done(id), self());
			queue.remove(id);
		}).build());
	}

}
