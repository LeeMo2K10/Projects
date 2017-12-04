package com.akka.externalserveices;

import java.util.Arrays;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.testkit.TestProbe;

public class IntegratingwithExternalServices {

	ActorSystem system = ActorSystem.create("External");

	final TestProbe probe = new TestProbe(system);
	Materializer mat = ActorMaterializer.create(system);

	LoggingAdapter log = Logging.getLogger(system, this);
	final AddressSystem addressSystem = new AddressSystem();
	final EmailServer emailServer = new EmailServer(probe.ref());

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

	public static final Hashtag AKKA = new Hashtag("#akka");

	public void mapAsync() {

		final Source<Author, NotUsed> authors = tweets.filter(t -> t.hashtags().contains(AKKA)).map(t -> t.author);

		final Source<String, NotUsed> emailAddresses = authors
				.mapAsync(4, author -> addressSystem.lookupEmail(author.handle)).filter(o -> o.isPresent())
				.map(o -> o.get());

		final RunnableGraph<NotUsed> sendEmails = emailAddresses
				.mapAsync(4, address -> emailServer.send(new Email(address, "Akka", "I like your tweet")))
				.to(Sink.foreach(a -> {
					System.out.println("Title : " + a.title + " Body :" + a.body + " To : " + a.to);
				}));

	sendEmails.run(mat);

	}

	public void mapAsyncUnordered() {

		final Source<Author, NotUsed> authors = tweets.filter(t -> t.hashtags().contains(AKKA)).map(t -> t.author);

		final Source<String, NotUsed> emailAddresses = authors
				.mapAsyncUnordered(4, author -> addressSystem.lookupEmail(author.handle)).filter(o -> o.isPresent())
				.map(o -> o.get());

		final RunnableGraph<NotUsed> sendEmails = emailAddresses
				.mapAsyncUnordered(4, address -> emailServer.send(new Email(address, "Akka", "I like your tweet")))
				.to(Sink.foreach(a -> {
					
					
					System.out.println("\n Title : " + a.title + " Body :" + a.body + " To : " + a.to);
				}));

		sendEmails.run(mat);
	}

	public static void main(String[] args) {

		IntegratingwithExternalServices se = new IntegratingwithExternalServices();
		se.mapAsync();
		se.mapAsyncUnordered();
	}

}