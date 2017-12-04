package com.spark.graphx;

import scala.Tuple2;
import scala.runtime.AbstractFunction1;

public class OrderFunction extends AbstractFunction1<Tuple2<Object, Object>, Object>{

	@Override
	public Object apply(Tuple2<Object, Object> tuple) {

		System.out.println(tuple);
		
		return tuple._2;
	}

}
