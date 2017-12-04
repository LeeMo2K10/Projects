package com.akka.remoting;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class CreationActor extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof MathOp) {
			ActorRef calculator = getContext().actorOf(Props.create(CalculatorActor.class));
			calculator.tell(message, getSelf());

		} else if (message instanceof MultiplicationResult) {
			MultiplicationResult result = (MultiplicationResult) message;
			System.out.printf("Mul result: %d * %d = %d\n", result.getN1(), result.getN2(), result.getResult());
			getContext().stop(getSender());

		} else if (message instanceof DivisionResult) {
			DivisionResult result = (DivisionResult) message;
			System.out.printf("Div result: %.0f / %d = %.2f\n", result.getN1(), result.getN2(), result.getResult());
			getContext().stop(getSender());

		} else {
			unhandled(message);
		}
	}
}
