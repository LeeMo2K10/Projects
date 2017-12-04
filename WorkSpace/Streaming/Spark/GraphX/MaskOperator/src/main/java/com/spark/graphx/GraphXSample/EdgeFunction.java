package com.spark.graphx.GraphXSample;

import java.io.Serializable;

import org.apache.spark.graphx.EdgeTriplet;

import scala.runtime.AbstractFunction1;

public class EdgeFunction extends AbstractFunction1<EdgeTriplet<String[], String>, Object> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public Object apply(EdgeTriplet<String[], String> id) {

		return !(id.srcAttr().equals("collab"));
	}

}