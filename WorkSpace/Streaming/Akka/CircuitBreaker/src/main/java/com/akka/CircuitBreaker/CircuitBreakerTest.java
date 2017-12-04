package com.akka.CircuitBreaker;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class CircuitBreakerTest {

	public void test() {
		ActorSystem system = ActorSystem.create("CircuitBreaker");
		ActorRef ref = system.actorOf(Props.create(DangerousJavaActor.class));
		ref.tell("is my middle name", null);

	}

	public static void main(String[] args) {
		CircuitBreakerTest test = new CircuitBreakerTest();
		test.test();
	}

}