package com.akka.transformation;

import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.Member;
import akka.cluster.MemberStatus;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import scala.Option;

public class TransformationBackend extends UntypedActor implements TransformMessages {
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	Cluster cluster = Cluster.get(getContext().system());

	@Override
	public void preRestart(Throwable reason, Option<Object> message) throws Exception {

		cluster.subscribe(getSelf(), MemberUp.class);

	}

	@Override
	public void postStop() throws Exception {
		cluster.unsubscribe(getSelf());
	}

	@Override
	public void onReceive(Object message) throws Exception {

		if (message instanceof TransformationJob) {
			TransformationJob job = (TransformationJob) message;
			log.info(job.getText());
			getSender().tell(new TransformationResult(job.getText().toUpperCase()), getSelf());
		} else if (message instanceof CurrentClusterState) {
			CurrentClusterState state = (CurrentClusterState) message;
			for (Member member : state.getMembers()) {
				if (member.status().equals(MemberStatus.up())) {
					register(member);
				}
			}
		} else if (message instanceof MemberUp) {
			MemberUp mUp = (MemberUp) message;
			register(mUp.member());

		} else {
			unhandled(message);
		}

	}

	private void register(Member member) {
		if (member.hasRole("frontend"))
			getContext().actorSelection(member.address() + "/user/frontend").tell(BACKEND_REGISTRATION, getSelf());
	}

}
