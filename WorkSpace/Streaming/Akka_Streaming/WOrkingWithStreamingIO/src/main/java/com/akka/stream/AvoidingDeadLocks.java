package com.akka.stream;

import java.util.concurrent.CompletionStage;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Framing;
import akka.stream.javadsl.FramingTruncation;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.Tcp;
import akka.stream.javadsl.Tcp.IncomingConnection;
import akka.stream.javadsl.Tcp.ServerBinding;
import akka.util.ByteString;

public class AvoidingDeadLocks {

	ActorSystem system = ActorSystem.create("ConnectingReplClient");

	Materializer mat = ActorMaterializer.create(system);

	LoggingAdapter log = Logging.getLogger(system, this);

	public void accept() {

		Source<IncomingConnection, CompletionStage<ServerBinding>> connections = Tcp.get(system).bind("localhost",
				8809);

		connections.runForeach(connection -> {

			final Flow<String, String, NotUsed> commandParser = Flow.<String>create()
					.takeWhile(elem -> !elem.equals("BYE")).map(elem -> elem + "!");

			final String welcomeMsg = "Welcome to: " + connection.localAddress() + " you are: "
					+ connection.remoteAddress() + "!";

			final Source<String, NotUsed> welcome = Source.single(welcomeMsg);

			final Flow<ByteString, ByteString, NotUsed> serverLogic = Flow.of(ByteString.class)
					.via(Framing.delimiter(ByteString.fromString("\n"), 256, FramingTruncation.DISALLOW))
					.map(ByteString::utf8String).via(commandParser).merge(welcome).map(s -> {

						log.info("" + s);
						return s + "\n";
					}).map(ByteString::fromString);

			connection.handleWith(serverLogic, mat);
		}, mat);

	}

	public static void main(String[] args) {
		AvoidingDeadLocks con = new AvoidingDeadLocks();
		con.accept();

	}

}
