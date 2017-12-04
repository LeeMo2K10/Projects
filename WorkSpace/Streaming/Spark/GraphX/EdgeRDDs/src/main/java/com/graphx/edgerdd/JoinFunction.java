package com.graphx.edgerdd;

import java.io.Serializable;

import scala.runtime.AbstractFunction4;

public class JoinFunction extends AbstractFunction4<Object, Object, Integer, Integer, Integer> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public Integer apply(Object a, Object b, Integer old, Integer extra) {

		return old + extra;
	}

}
