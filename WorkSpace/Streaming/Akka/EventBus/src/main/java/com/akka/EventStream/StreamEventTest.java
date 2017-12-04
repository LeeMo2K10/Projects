package com.akka.EventStream;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.DeadLetter;
import akka.actor.Props;

public class StreamEventTest {

	public void test() {
		final ActorSystem system = ActorSystem.create("DeadLetters");
		final ActorRef actor = system.actorOf(Props.create(DeadLetterActor.class));
		system.eventStream().subscribe(actor, DeadLetter.class);
	}

	public static void main(String[] args) {
		StreamEventTest test = new StreamEventTest();
		test.test();
	}
}
