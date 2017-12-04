package com.akka.future;

import static akka.dispatch.Futures.future;

import java.util.Arrays;
import java.util.concurrent.Callable;

import akka.actor.ActorSystem;
import akka.dispatch.Futures;
import akka.pattern.Patterns;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class Future_After {
	ActorSystem system = ActorSystem.create("ActorSystem");

	public void test() {
		final ExecutionContext ec = system.dispatcher();
		Future<String> fail = Futures.failed(new IllegalStateException("OHNoex"));
		Future<String> delayed = Patterns.after(Duration.create(200, "millis"), system.scheduler(), ec, fail);
		Future<String> future = future(new Callable<String>() {

			@Override
			public String call() throws InterruptedException {
				Thread.sleep(210);

				return "Foo";
			}
		}, ec);
		Future<String> f1 = Futures.firstCompletedOf(Arrays.<Future<String>>asList(future, delayed), ec);
		f1.onSuccess(new PrintResult<String>(), system.dispatcher());
		f1.onFailure(new PrintResult<Throwable>(), system.dispatcher());

	}

	public static void main(String[] args) {
		Future_After fm = new Future_After();
		fm.test();
	}

}
