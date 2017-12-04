package com.akka.stream;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.FlowShape;
import akka.stream.Materializer;
import akka.stream.UniformFanInShape;
import akka.stream.UniformFanOutShape;
import akka.stream.javadsl.Balance;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.GraphDSL;
import akka.stream.javadsl.Merge;

public class ParallelProcessing {

	ActorSystem system = ActorSystem.create("Pipelining");
	Materializer mat = ActorMaterializer.create(system);

	LoggingAdapter log = Logging.getLogger(system, this);

	public void pipeline() {

		Flow<ScoopOfBatter, Pancake, NotUsed> fryingPan = Flow.of(ScoopOfBatter.class).map(batter -> new Pancake());

		Flow.fromGraph(GraphDSL.create(b -> {

			final UniformFanInShape<Pancake, Pancake> mergePancakes = b.add(Merge.create(2));

			final UniformFanOutShape<ScoopOfBatter, ScoopOfBatter> dispatchBatter = b.add(Balance.create(2));

			b.from(dispatchBatter.out(0)).via(b.add(fryingPan.async())).toInlet(mergePancakes.in(0));

			b.from(dispatchBatter.out(1)).via(b.add(fryingPan.async())).toInlet(mergePancakes.in(1));

			return FlowShape.of(dispatchBatter.in(), mergePancakes.out());
		}));

	}

	public static void main(String[] args) {

		ParallelProcessing par = new ParallelProcessing();
		par.pipeline();
	}

}