package com.biz.akka;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ActorCreation extends UntypedActor {
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof String) {
			log.info("Recived String Message :" + message);
			getSender().tell(message, getSelf());
		} else {
			unhandled(message);
		}

	}

}
