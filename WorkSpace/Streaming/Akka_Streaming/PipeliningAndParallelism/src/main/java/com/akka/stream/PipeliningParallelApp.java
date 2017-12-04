package com.akka.stream;

import java.util.Arrays;
import java.util.concurrent.CompletionStage;

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
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Merge;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class PipeliningParallelApp {

	final ActorSystem system = ActorSystem.create("Pipelining");
	final Materializer mat = ActorMaterializer.create(system);
	LoggingAdapter log = Logging.getLogger(system, this);

	@SuppressWarnings("static-access")
	public  void parellel() {

		Flow<ScoopOfBatter, HalfCookedPancake, NotUsed> fryingPan1 = Flow.of(ScoopOfBatter.class)
				.map(batter -> new HalfCookedPancake());

		Flow<HalfCookedPancake, Pancake, NotUsed> fryingPan2 = Flow.of(HalfCookedPancake.class)
				.map(halfCooked -> new Pancake());

		Flow<ScoopOfBatter, Pancake, NotUsed> pancakeChef = Flow.fromGraph(GraphDSL.create(b -> {

			final UniformFanInShape<Pancake, Pancake> mergePancakes = b.add(Merge.create(2));
			final UniformFanOutShape<ScoopOfBatter, ScoopOfBatter> dispatchBatter = b.add(Balance.create(2));

			b.from(dispatchBatter.out(0)).via(b.add(fryingPan1.async())).via(b.add(fryingPan2.async()))
					.toInlet(mergePancakes.in(0));

			b.from(dispatchBatter.out(1)).via(b.add(fryingPan1.async())).via(b.add(fryingPan2.async()))
					.toInlet(mergePancakes.in(1));

			return FlowShape.of(dispatchBatter.in(), mergePancakes.out());

		}));

		Sink<Integer, CompletionStage<Integer>> sink = pancakeChef.of(Integer.class).map(f -> f * 1)
				.toMat(Sink.fold(0, (agg, next) -> agg + next), Keep.right());

		CompletionStage<Integer> result = Source.from(Arrays.asList(1, 2, 3, 4, 5, 5, 1, 2)).runWith(sink, mat);

		result.thenAcceptAsync(f -> {
			log.info("" + f);
		});

	}

	public static void main(String[] args) {

		PipeliningParallelApp app = new PipeliningParallelApp();
		app.parellel();
	}
}