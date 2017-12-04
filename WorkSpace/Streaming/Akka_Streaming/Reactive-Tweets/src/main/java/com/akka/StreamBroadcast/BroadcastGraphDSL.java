package com.akka.StreamBroadcast;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.concurrent.CompletionStage;

import com.akka.stream.Hashtag;
import com.akka.stream.Tweets;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.function.Procedure;
import akka.stream.ActorMaterializer;
import akka.stream.ClosedShape;
import akka.stream.FlowShape;
import akka.stream.Materializer;
import akka.stream.SinkShape;
import akka.stream.UniformFanOutShape;
import akka.stream.javadsl.Broadcast;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.GraphDSL;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class BroadcastGraphDSL {

	private final ActorSystem system = ActorSystem.create("akka-reactive-tweets");
	private final LoggingAdapter log = Logging.getLogger(system, this);

	final Materializer materializer = ActorMaterializer.create(system);
	Sink<String, CompletionStage<Done>> writeAuthors = Sink.foreach(new Procedure<String>() {
		private static final long serialVersionUID = 1L;

		@Override
		public void apply(String s) throws Exception {
			System.out.println(s);

		}
	});
	Sink<Hashtag, CompletionStage<Done>> writeHashtags = Sink.foreach(new Procedure<Hashtag>() {

		private static final long serialVersionUID = 1L;

		public void apply(Hashtag h) throws Exception {
			System.out.println(h.name);
		};
	});

	public void test() {

		final Source<Tweets, NotUsed> tweets = Source.from(Arrays.asList(
				new Tweets("pepe", 1000, "Wow! #akka is great"), new Tweets("juan", 2000, "I am trendy, guys!"),
				new Tweets("maria", 3000, "#This #is #an #instagram #tweet"),
				new Tweets("ilitri", 4000, "Ou yeah! usaré #akka en el foro")));

		RunnableGraph.fromGraph(GraphDSL.create(b -> {
			log.info("");
			final UniformFanOutShape<Tweets, Tweets> bcast = b.add(Broadcast.create(2));
			final FlowShape<Tweets, String> toAuthor = b.add(Flow.of(Tweets.class).map(t -> t.author));
			final FlowShape<Tweets, Hashtag> toTags = b
					.add(Flow.of(Tweets.class).mapConcat(t -> new ArrayList<Hashtag>(t.hashtags())));
			final SinkShape<String> authors = b.add(writeAuthors);
			final SinkShape<Hashtag> hashtags = b.add(writeHashtags);
			b.from(b.add(tweets)).viaFanOut(bcast).via(toAuthor).to(authors);
			b.from(bcast).via(toTags).to(hashtags);
			return ClosedShape.getInstance();

		})).run(materializer);

	}

	public static void main(String[] args) {
		BroadcastGraphDSL dsl = new BroadcastGraphDSL();
		dsl.test();
	}

}
