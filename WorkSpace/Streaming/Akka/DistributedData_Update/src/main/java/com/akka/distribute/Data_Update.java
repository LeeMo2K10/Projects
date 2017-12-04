package com.akka.distribute;

import java.util.concurrent.TimeUnit;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.cluster.Cluster;
import akka.cluster.ddata.DistributedData;
import akka.cluster.ddata.Flag;
import akka.cluster.ddata.FlagKey;
import akka.cluster.ddata.GSet;
import akka.cluster.ddata.GSetKey;
import akka.cluster.ddata.Key;
import akka.cluster.ddata.ORSet;
import akka.cluster.ddata.ORSetKey;
import akka.cluster.ddata.PNCounter;
import akka.cluster.ddata.PNCounterKey;
import akka.cluster.ddata.Replicator;
import akka.cluster.ddata.Replicator.Changed;
import akka.cluster.ddata.Replicator.Subscribe;
import akka.cluster.ddata.Replicator.Update;
import akka.cluster.ddata.Replicator.UpdateResponse;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import scala.concurrent.duration.Duration;
import scala.concurrent.forkjoin.ThreadLocalRandom;

public class Data_Update extends AbstractActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private static final String TICK = "tick";

	private final ActorRef replicator = DistributedData.get(getContext().system()).replicator();
	private final Cluster node = Cluster.get(context().system());

	private final Cancellable tickTask = context().system().scheduler().schedule(Duration.create(5, TimeUnit.SECONDS),
			Duration.create(5, TimeUnit.SECONDS), self(), TICK, context().dispatcher(), self());

	final Key<PNCounter> counter1Key = PNCounterKey.create("counter1");
	final Key<GSet<String>> set1Key = GSetKey.create("set1");
	final Key<ORSet<String>> set2Key = ORSetKey.create("set2");
	final Key<Flag> activeFlagKey = FlagKey.create("active");

	public Data_Update() {
		receive(ReceiveBuilder.match(String.class, a -> a.equals(TICK), a -> receiveTick())
				// .match(Changed.class, c -> c.key().equals(set2Key), c ->
				// receiveChanged((Changed<ORSet<String>>) c))
				.match(Changed.class, c -> c.key().equals(counter1Key), c -> {
					PNCounter incrementedValue = (PNCounter) c.dataValue();
					log.info("PNCounter value : " + incrementedValue.value());
				}).match(UpdateResponse.class, r -> receiveUpdateResoponse()).build());

	}

	private void receiveTick() {
		replicator.tell(new Replicator.Update<PNCounter>(counter1Key, PNCounter.create(), Replicator.writeLocal(),
				curr -> curr.increment(node, 1)), self());
	}

	@SuppressWarnings("unused")
	private void receiveChanged(Changed<ORSet<String>> c) {
		ORSet<String> data = c.dataValue();
		log.info("Current elements: {}", data.getElements());
	}

	private void receiveUpdateResoponse() {

	}

	@Override
	public void preStart() {
		Subscribe<PNCounter> subscribe = new Subscribe<>(counter1Key, self());
		replicator.tell(subscribe, ActorRef.noSender());
	}

	@Override
	public void postStop() {
		tickTask.cancel();
	}

}
