package com.akka.distribute;

import java.math.BigInteger;
import java.util.concurrent.Callable;

import akka.actor.UntypedActor;
import akka.dispatch.Mapper;
import scala.concurrent.Future;
import static akka.pattern.Patterns.pipe;
import static akka.dispatch.Futures.future;

public class FactorialBackend extends UntypedActor {

	@Override
	public void onReceive(Object msg) throws Exception {

		if (msg instanceof Integer) {
			final Integer n = (Integer) msg;
			Future<BigInteger> f1 = future(new Callable<BigInteger>() {

				@Override
				public BigInteger call() throws Exception {
					return factorial(n);
				}

			}, getContext().dispatcher());

			Future<FactorialResult> result = f1.map(new Mapper<BigInteger, FactorialResult>() {
				public FactorialResult apply(BigInteger factorial) {
					return new FactorialResult(n, factorial);
				}
			}, getContext().dispatcher());

			pipe(result, getContext().dispatcher()).to(getSender());

		} else {
			unhandled(msg);
		}

	}

	BigInteger factorial(int n) {
		BigInteger acc = BigInteger.ONE;
		for (int i = 1; i <= n; ++i) {
			acc = acc.multiply(BigInteger.valueOf(i));
		}
		return acc;
	}

}