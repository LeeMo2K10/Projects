package com.akka.stream;

import java.util.concurrent.CompletionStage;

import akka.Done;
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

public class AcceptingConnections {

	ActorSystem system = ActorSystem.create("External");

	Materializer mat = ActorMaterializer.create(system);

	LoggingAdapter log = Logging.getLogger(system, this);

	public void accept() {

		Source<IncomingConnection, CompletionStage<ServerBinding>> connections = Tcp.get(system).bind("localhost",
				8809);

		CompletionStage<Done> result = connections.runForeach(connection1 -> {
			log.info("New Connection From :" + connection1.remoteAddress());

			Flow<ByteString, ByteString, NotUsed> echo = Flow.of(ByteString.class)
					.via(Framing.delimiter(ByteString.fromString("\n"), 256, FramingTruncation.ALLOW))
					.map(ByteString::utf8String).map(s -> {
						System.out.println(s);
						return s + "!!!\n";
					}).map(ByteString::fromString);

			connection1.handleWith(echo, mat);

		}, mat);

		result.thenAcceptAsync(f -> {
			log.info("" + f);
		});

	}

	public static void main(String[] args) {
		AcceptingConnections con = new AcceptingConnections();
		con.accept();

	}

}
