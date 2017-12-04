package com.biz.akka;

import com.biz.akka.HelloAkka.Greeter;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class CreateMain {
	public void test() {
		final ActorSystem system = ActorSystem.create("MySystem");
	
		final ActorRef ref = system.actorOf(Props.create(ActorCreation.class), "creationActor");
		
		final ActorRef ref1 = system.actorOf(Props.create(Greeter.class), "greeter");
		
		ref.tell("Hiiiiiiiiiiiii ", ref1);
	}

	public static void main(String[] args) {
		CreateMain main = new CreateMain();
		main.test();
	}
}