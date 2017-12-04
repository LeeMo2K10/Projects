package com.graphx.vertexrdd;

import java.io.Serializable;
import java.util.Arrays;

import scala.Option;
import scala.runtime.AbstractFunction0;
import scala.runtime.AbstractFunction3;

public class JoinFunction extends AbstractFunction3<Object, Integer, Option<Integer>, Integer> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public Integer apply(Object id, Integer old, Option<Integer> extra) {

		return old + extra.getOrElse(new F0());
	}

	class F0 extends AbstractFunction0<Integer> {

		@Override
		public Integer apply() {
			return 100;
		}

	}
}