package com.akka.io;

import java.net.InetSocketAddress;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.io.Tcp;
import akka.io.Tcp.Bind;
import akka.io.Tcp.CommandFailed;
import akka.io.Tcp.Connected;
import akka.io.TcpMessage;

public class Server extends UntypedActor {
	InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8081);

	ActorRef manager;

	public Server(ActorRef manager) {

		this.manager = manager;

	}

	public static Props props(ActorRef manager) {
		return Props.create(Server.class, manager);

	}

	public void preStart() {

		ActorRef tcp = Tcp.get(getContext().system()).manager();
		tcp.tell(TcpMessage.bind(getSelf(), new InetSocketAddress("127.0.0.1", 8081), 100), getSelf());

	}

	@Override
	public void onReceive(Object msg) throws Exception {
		System.out.println(msg);
		if (msg instanceof Bind) {
			manager.tell(msg, getSelf());
		} else if (msg instanceof CommandFailed) {
			getContext().stop(getSelf());
		} else if (msg instanceof Connected) {
			Connected conn = (Connected) msg;
			manager.tell(conn, getSelf());
			final ActorRef handler = getContext().actorOf(Props.create(SimpleEchoHandler.class, getSelf(), address));
			getSender().tell(TcpMessage.register(handler, true, true), getSelf());
		}

	}

}
