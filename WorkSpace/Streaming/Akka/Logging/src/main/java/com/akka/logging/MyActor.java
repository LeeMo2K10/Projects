package com.akka.logging;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.Option;

public class MyActor extends UntypedActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Override
	public void preStart() throws Exception {

		log.info("Starting ...");
	}

	@Override
	public void preRestart(Throwable reason, Option<Object> message) throws Exception {

		log.error(reason, "Restarting due to [{}] when processing [{}]", reason.getMessage(),
				message.isDefined() ? message.get() : "");
	}

	@Override
	public void onReceive(Object msg) throws Exception {

		if (msg.equals("test")) {
			log.info("Received test");
		} else {
			log.warning("Received unknown message: {}", msg);
		}
	}

}
