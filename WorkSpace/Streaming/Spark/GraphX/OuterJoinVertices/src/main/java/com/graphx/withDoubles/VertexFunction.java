package com.graphx.withDoubles;

import java.io.Serializable;
import java.util.Arrays;

import scala.Option;
import scala.runtime.AbstractFunction0;
import scala.runtime.AbstractFunction3;

public class VertexFunction extends AbstractFunction3<Object, Double, Option<Double>, Double> implements Serializable {

	
	private static final long serialVersionUID = 1L;

	@Override
	public Double apply(Object id, Double old, Option<Double> extra) {
		System.out.println(Arrays.asList(old));

		return old + extra.getOrElse(new F0());
	}

	class F0 extends AbstractFunction0<Double> {

		@Override
		public Double apply() {
			return 100.0;
		}

	}

}