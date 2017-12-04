package com.akka.distribute;

import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.ReceiveTimeout;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.FromConfig;
import akka.util.Timeout;
import scala.concurrent.duration.Duration;

public class FactorialFrontend extends UntypedActor {

	int upToN = 0;
	boolean repeat = true;

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	ActorRef backend = getContext().actorOf(FromConfig.getInstance().props(), "factorialBackendRouter");

	public FactorialFrontend(int upToN, boolean repeat) {
		this.upToN = upToN;
		this.repeat = repeat;
	}

	@Override
	public void preStart() throws Exception {
		sendJobs();
		getContext().setReceiveTimeout(Duration.create(10, TimeUnit.SECONDS));
		super.preStart();
	}

	@Override
	public void onReceive(Object msg) throws Exception {

		if (msg instanceof FactorialResult) {
			FactorialResult result = (FactorialResult) msg;
			if (result.n == upToN)
				log.debug("" + result.n + result.factorial);
			if (repeat)
				sendJobs();
			else
				getContext().stop(getSelf());

		} else if (msg instanceof ReceiveTimeout) {
			log.info("Timeout");
			sendJobs();

		} else {
			unhandled(msg);
		}

	}

	void sendJobs() {
		log.info("Starting batch of factorials up to [{}]", upToN);
		for (int n = 1; n <= upToN; n++) {
			backend.tell(n, getSelf());
		}
	}

}