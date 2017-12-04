package com.akka.stream;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;

public class Pipelining {

	ActorSystem system = ActorSystem.create("Pipelining");
	Materializer mat = ActorMaterializer.create(system);

	@SuppressWarnings("unused")
	public void pipeline() {

		Flow<ScoopOfBatter, HalfCookedPancake, NotUsed> fryingPan1 = Flow.of(ScoopOfBatter.class)
				.map(batter -> new HalfCookedPancake());

		Flow<HalfCookedPancake, Pancake, NotUsed> fryingPan2 = Flow.of(HalfCookedPancake.class)
				.map(halfCooked -> new Pancake());

		Flow<ScoopOfBatter, Pancake, NotUsed> pancake = fryingPan1.async().via(fryingPan2.async());

	}

}

class ScoopOfBatter {
}

class HalfCookedPancake {
}

class Pancake {
}