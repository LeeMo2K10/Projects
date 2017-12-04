package com.Spark;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class FlatMapExample {
	static Logger logger = Logger.getLogger(FlatMapExample.class);
	SparkConf conf = new SparkConf().setAppName("FlatMapExample").setMaster("spark://master:7077");

	public void test() {
		JavaSparkContext sc = new JavaSparkContext(conf);
		JavaRDD<String> rddX = sc.parallelize(Arrays.asList("spark rdd example", "sample example"), 2);

		// List Using Map
		JavaRDD<String[]> rddY = rddX.map(e -> e.split(" "));
		List<String[]> listUsingMap = rddY.collect();
		logger.info("List Using Map Transformation : " + listUsingMap);

		// List Using FlatMap
		JavaRDD<String> rddY2 = rddX.flatMap(e -> Arrays.asList(e.split(" ")));
		List<String> listFlatMap = rddY2.collect();
		logger.info("List using FlatMap Transformation : " + listFlatMap);

	}

	public static void main(String[] args) {
		FlatMapExample ex = new FlatMapExample();
		ex.test();
	}
}