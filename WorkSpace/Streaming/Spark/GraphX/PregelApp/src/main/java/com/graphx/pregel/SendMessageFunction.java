package com.graphx.pregel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;

import org.apache.spark.graphx.EdgeTriplet;

import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.JavaConversions;
import scala.runtime.AbstractFunction1;

public class SendMessageFunction extends
		AbstractFunction1<EdgeTriplet<Integer, Integer>, Iterator<Tuple2<Object, Integer>>> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public Iterator<Tuple2<Object, Integer>> apply(EdgeTriplet<Integer, Integer> triplet) {

		System.out.println("Src Attr : " + triplet.srcAttr());

		System.out.println("Edge  Attr : " + triplet.attr());

		System.out.println("Dest Attr : " + triplet.dstAttr());

		if (triplet.srcAttr() + triplet.attr() < triplet.dstAttr())
			return JavaConversions.asScalaIterator(
					Arrays.asList(new Tuple2<Object, Integer>(triplet.dstId(), triplet.srcAttr() + triplet.dstAttr()))
							.iterator());
		else
			return JavaConversions.asScalaIterator(Collections.emptyIterator());
	}
}
