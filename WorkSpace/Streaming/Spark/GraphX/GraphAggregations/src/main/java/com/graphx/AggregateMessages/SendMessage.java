package com.graphx.AggregateMessages;

import java.io.Serializable;

import org.apache.spark.graphx.EdgeContext;

import scala.Tuple2;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxedUnit;

public class SendMessage extends AbstractFunction1<EdgeContext<Object, Object, Tuple2<Integer, Long>>, BoxedUnit>
		implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public BoxedUnit apply(EdgeContext<Object, Object, Tuple2<Integer, Long>> triplet) {

		if ((Long) triplet.srcAttr() > (Long) triplet.dstAttr()) {

			triplet.sendToDst(new Tuple2<Integer, Long>(1, (Long) triplet.srcAttr()));

		}
		return BoxedUnit.UNIT;
	}

}