package com.akka.logging;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class MdcActorTest {

	public void test() {

		ActorSystem system = ActorSystem.create("Logging");
		ActorRef logref = system.actorOf(Props.create(MdcActor.class));

		logref.tell("Hello", null);

	}

	public static void main(String[] args) {
		MdcActorTest test = new MdcActorTest();
		test.test();
	}

}
