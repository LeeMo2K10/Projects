package com.akka;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Actor1 extends UntypedActor {
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Override
	public void onReceive(Object msg) throws Exception {

		if (msg instanceof String) {

			log.info("Recieved Message From Main Is : " + msg);

			String message = (String) msg;

			ActorSelection actorpath = getContext().system().actorSelection("akka://Notifier/user/Actor2");

			actorpath.anchor();

			actorpath.tell(message, getSender());

		}
	}
}
