package com.akka.utilities;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class LookUpBusTest {

	ActorSystem system = ActorSystem.create("EventBus");
	LoggingAdapter log = Logging.getLogger(system, this);

	public void test() {

		LookupBusImpl lookupBus = new LookupBusImpl();
		lookupBus.subscribe(system.actorOf(Props.create(Myactor.class), "Myactor"), "time");
		lookupBus.publish(new MsgEnvelope("time", System.currentTimeMillis()));
		lookupBus.publish(new MsgEnvelope("greetings", "hello"));

	}

	public static void main(String[] args) {
		LookUpBusTest test = new LookUpBusTest();
		test.test();
	}
}
