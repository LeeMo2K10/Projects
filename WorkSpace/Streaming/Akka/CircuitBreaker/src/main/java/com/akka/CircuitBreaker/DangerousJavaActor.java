package com.akka.CircuitBreaker;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.CircuitBreaker;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import static akka.pattern.Patterns.pipe;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static akka.dispatch.Futures.future;

public class DangerousJavaActor extends UntypedActor {

	private final CircuitBreaker breaker;
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	public DangerousJavaActor() {
		this.breaker = new CircuitBreaker(getContext().dispatcher(), getContext().system().scheduler(), 5,
				Duration.create(10, TimeUnit.SECONDS), Duration.create(1, TimeUnit.MILLISECONDS))
						.onOpen(new Runnable() {
							public void run() {
								notifyMeOnOpen();
							}
						});
	}

	public void notifyMeOnOpen() {
		log.warning("My CircuitBreaker is now open, and will not close for one minute");
	}

	public String dangerousCall() {
		return "This really isn't that dangerous of a call after all";
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof String) {
			String m = (String) message;
			log.info("Recieved Message Is : " + message);

			if ("is my middle name".equals(m)) {
				pipe(breaker.callWithCircuitBreaker(new Callable<Future<String>>() {
					public Future<String> call() throws Exception {
						return future(new Callable<String>() {
							public String call() throws Exception {
								return dangerousCall();
							}
						}, getContext().dispatcher());
					}
				}), getContext().dispatcher()).to(getSender());
			}
			if ("block for me".equals(m)) {
				getSender().tell(breaker.callWithSyncCircuitBreaker(new Callable<String>() {
					@Override
					public String call() throws Exception {
						return dangerousCall();
					}
				}), getSelf());
			}
		}
	}
}
