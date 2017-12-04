package com.Spark;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;

import scala.Tuple2;

public class ReduceByKeyExample {
	static Logger logger = Logger.getLogger(ReduceByKeyExample.class);

	public void test() {
		SparkConf conf = new SparkConf().setAppName("FilterExample").setMaster("spark://master:7077");
		JavaSparkContext sc = new JavaSparkContext(conf);
		Function2<Integer, Integer, Integer> reduceSumFunc = (accum, n) -> (accum + n);

		JavaRDD<String> x = sc.parallelize(Arrays.asList("a", "b", "a", "a", "b", "b", "b", "b"), 3);
		JavaPairRDD<String, Integer> rddX = x.mapToPair(e -> new Tuple2<String, Integer>(e, 1));

		// New JavaPairRDD
		JavaPairRDD<String, Integer> rddY = rddX.reduceByKey(reduceSumFunc);
		for (Tuple2<String, Integer> element : rddY.collect()) {
			logger.info("Reduce : " + "(" + element._1 + ", " + element._2 + ")");
		}

	}

	public static void main(String[] args) {
		ReduceByKeyExample ex = new ReduceByKeyExample();
		ex.test();
	}
}
