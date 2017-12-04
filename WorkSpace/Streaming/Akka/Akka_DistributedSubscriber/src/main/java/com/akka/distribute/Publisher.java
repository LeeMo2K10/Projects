package com.akka.distribute;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.Option;

public class Publisher extends UntypedActor {
	Cluster cluster = Cluster.get(getContext().system());

	@Override
	public void preRestart(Throwable reason, Option<Object> message) throws Exception {

		cluster.subscribe(getSelf(), MemberUp.class);

	}

	@Override
	public void postStop() throws Exception {
	//	cluster.unsubscribe(getSelf());
	}
	
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();

	@Override
	public void onReceive(Object msg) throws Exception {

		if (msg instanceof String) {
			String in = (String) msg;
			String out = in.toUpperCase();
			log.info(out);

			mediator.tell(new DistributedPubSubMediator.Publish("content", out), getSelf());

		} else {
			unhandled(msg);
		}
	}

}