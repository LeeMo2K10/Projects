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

public class ParallelizedPipeliningApp {

	ActorSystem system = ActorSystem.create("Pipelining");
	Materializer mat = ActorMaterializer.create(system);

	LoggingAdapter log = Logging.getLogger(system, this);

	@SuppressWarnings("static-access")
	public void parellel() {

		Flow<ScoopOfBatter, HalfCookedPancake, NotUsed> fryingPan1 = Flow.of(ScoopOfBatter.class)
				.map(batter -> new HalfCookedPancake());

		Flow<HalfCookedPancake, Pancake, NotUsed> fryingPan2 = Flow.of(HalfCookedPancake.class)
				.map(halfCooked -> new Pancake());

		Flow<ScoopOfBatter, HalfCookedPancake, NotUsed> pancakeChefs1 = Flow.fromGraph(GraphDSL.create(b -> {
			final UniformFanInShape<HalfCookedPancake, HalfCookedPancake> mergeHalfCooked = b.add(Merge.create(2));
			final UniformFanOutShape<ScoopOfBatter, ScoopOfBatter> dispatchBatter = b.add(Balance.create(2));

			// Two chefs work with one frying pan for each, half-frying the
			// pancakes then putting
			// them into a common pool
			b.from(dispatchBatter.out(0)).via(b.add(fryingPan1.async())).toInlet(mergeHalfCooked.in(0));
			b.from(dispatchBatter.out(1)).via(b.add(fryingPan1.async())).toInlet(mergeHalfCooked.in(1));

			return FlowShape.of(dispatchBatter.in(), mergeHalfCooked.out());
		}));

		Flow<HalfCookedPancake, Pancake, NotUsed> pancakeChefs2 = Flow.fromGraph(GraphDSL.create(b -> {
			final UniformFanInShape<Pancake, Pancake> mergePancakes = b.add(Merge.create(2));
			final UniformFanOutShape<HalfCookedPancake, HalfCookedPancake> dispatchHalfCooked = b
					.add(Balance.create(2));

			// Two chefs work with one frying pan for each, finishing the
			// pancakes then putting
			// them into a common pool
			b.from(dispatchHalfCooked.out(0)).via(b.add(fryingPan2.async())).toInlet(mergePancakes.in(0));
			b.from(dispatchHalfCooked.out(1)).via(b.add(fryingPan2.async())).toInlet(mergePancakes.in(1));

			return FlowShape.of(dispatchHalfCooked.in(), mergePancakes.out());
		}));
		Flow<ScoopOfBatter, Pancake, NotUsed> kitchen = pancakeChefs1.via(pancakeChefs2);

		Sink<Integer, CompletionStage<Integer>> sink = kitchen.of(Integer.class).map(f -> f * 1)
				.toMat(Sink.fold(0, (agg, next) -> agg + next), Keep.right());

		CompletionStage<Integer> result = Source.from(Arrays.asList(1, 2, 3, 4, 5, 5, 1, 2)).runWith(sink, mat);

		result.thenAcceptAsync(f -> {
			log.info("" + f);
		});
	}

	public static void main(String[] args) {

		ParallelizedPipeliningApp app = new ParallelizedPipeliningApp();

		app.parellel();
	}

}