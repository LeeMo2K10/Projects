package com.akka.stream;

import akka.japi.Function;
import akka.stream.Attributes;
import akka.stream.FlowShape;
import akka.stream.Inlet;
import akka.stream.Outlet;
import akka.stream.stage.AbstractInHandler;
import akka.stream.stage.AbstractOutHandler;
import akka.stream.stage.GraphStage;
import akka.stream.stage.GraphStageLogic;

public class Map<A, B> extends GraphStage<FlowShape<A, B>> {

	public final Function<A, B> f;

	public Map(Function<A, B> f) {

		this.f = f;
	}

	Inlet<A> in = Inlet.create("Map.in");
	Outlet<B> out = Outlet.create("Map.out");

	FlowShape<A, B> shape = FlowShape.of(in, out);

	@Override
	public FlowShape<A, B> shape() {
		return shape;
	}

	@Override
	public GraphStageLogic createLogic(Attributes inheritedAttributes) {
		return new GraphStageLogic(shape) {
			{
				setHandler(in, new AbstractInHandler() {

					@Override
					public void onPush() throws Exception {

						push(out, f.apply(grab(in)));
					}

				});
				setHandler(out, new AbstractOutHandler() {

					@Override
					public void onPull() throws Exception {
						pull(in);
					}
				});
			}
		};
	}

}
