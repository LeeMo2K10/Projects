package com.akka.future;

import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import scala.Tuple2;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;

public class Future_Zip {
	ActorSystem system = ActorSystem.create("ActorSystem");

	public void test() {
		final ExecutionContext ec = system.dispatcher();
		Future<String> f1 = Futures.successful("Foo");
		Future<String> f2 = Futures.successful("Bar");
		Future<String> f4 = f1.zip(f2).map(new Mapper<Tuple2<String, String>, String>() {
			@Override
			public String apply(Tuple2<String, String> zipped) {
				// TODO Auto-generated method stub
				return zipped._1 + zipped._2;
			}
		}, ec);

		f4.onSuccess(new PrintResult<String>(), system.dispatcher());

	}

	public static void main(String[] args) {
		Future_Zip fm = new Future_Zip();
		fm.test();
	}

}
