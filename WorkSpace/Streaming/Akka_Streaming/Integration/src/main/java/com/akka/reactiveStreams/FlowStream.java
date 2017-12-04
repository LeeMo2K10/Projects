package com.akka.reactiveStreams;

import java.util.Arrays;

import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import com.akka.externalserveices.Author;
import com.akka.externalserveices.Hashtag;
import com.akka.externalserveices.RS;
import com.akka.externalserveices.Tweet;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.AsPublisher;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.testkit.TestProbe;

public class FlowStream {

	ActorSystem system = ActorSystem.create("External");

	final TestProbe probe = new TestProbe(system);
	Materializer mat = ActorMaterializer.create(system);

	LoggingAdapter log = Logging.getLogger(system, this);
	public static final Hashtag AKKA = new Hashtag("#akka");

	TestProbe storageProbe = new TestProbe(system);
	TestProbe alertProbe = new TestProbe(system);

	public static final Source<Tweet, NotUsed> tweets = Source.from(
			Arrays.asList(new Tweet[] { new Tweet(new Author("rolandkuhn"), System.currentTimeMillis(), "#akka rocks!"),
					new Tweet(new Author("patriknw"), System.currentTimeMillis(), "#akka !"),
					new Tweet(new Author("bantonsson"), System.currentTimeMillis(), "#akka !"),
					new Tweet(new Author("drewhk"), System.currentTimeMillis(), "#akka !"),
					new Tweet(new Author("ktosopl"), System.currentTimeMillis(), "#akka on the rocks!"),
					new Tweet(new Author("mmartynas"), System.currentTimeMillis(), "wow #akka !"),
					new Tweet(new Author("akkateam"), System.currentTimeMillis(), "#akka rocks!"),
					new Tweet(new Author("bananaman"), System.currentTimeMillis(), "#bananas rock!"),
					new Tweet(new Author("appleman"), System.currentTimeMillis(), "#apples rock!"),
					new Tweet(new Author("drama"), System.currentTimeMillis(), "we compared #apples to #oranges!") }));

	public void flow() {

		RS rs = new RS() {

			@Override
			public Publisher<Tweet> tweets() {
				return tweets.runWith(Sink.asPublisher(AsPublisher.WITHOUT_FANOUT), mat);
			}

			@Override
			public Subscriber<Author> storage() {
				return new MinimalProbeSubscriber<>(storageProbe.ref());

			}

			@Override
			public Subscriber<Author> alert() {
				return new MinimalProbeSubscriber<>(alertProbe.ref());
			}
		};

		final Flow<Tweet, Author, NotUsed> authors = Flow.of(Tweet.class).filter(t -> t.hashtags().contains(AKKA))
				.map(t -> t.author);

		final Processor<Tweet, Author> processor = authors.toProcessor().run(mat);

		rs.tweets().subscribe(processor);
		processor.subscribe(rs.storage());

		assertStorageResult();

	}

	private void assertStorageResult() {

		storageProbe.expectMsg(new Author("rolandkuhn"));
		storageProbe.expectMsg(new Author("patriknw"));
		storageProbe.expectMsg(new Author("bantonsson"));
		storageProbe.expectMsg(new Author("drewhk"));
		storageProbe.expectMsg(new Author("ktosopl"));
		storageProbe.expectMsg(new Author("mmartynas"));
		storageProbe.expectMsg(new Author("akkateam"));
		storageProbe.expectMsg("complete");

	}

	public static void main(String[] args) {

		FlowStream str = new FlowStream();
		str.flow();
	}
}
