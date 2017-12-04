package com.spark.graphx;

import java.io.Serializable;

import scala.runtime.AbstractFunction2;

public class VertexFunction2 extends AbstractFunction2<Object, String, Object> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public Object apply(Object id, String attr) {

		return attr.split(" ").length == 2;
	}

}
