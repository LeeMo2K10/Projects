package com.Spark;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

public class MapExample {
	static Logger logger = Logger.getLogger(MapExample.class);

	public void test() {
		SparkConf conf = new SparkConf().setAppName("MapExample").setMaster("spark://master:7077");
		JavaSparkContext sc = new JavaSparkContext(conf);
		JavaRDD<String> lines = sc.parallelize(Arrays.asList("spark", "rdd", "example", "sample", "example"));
		JavaRDD<Tuple2<String, Integer>> y = lines.map(e -> new Tuple2<String, Integer>(e, 1));
		List<Tuple2<String, Integer>> list1 = y.collect();
		logger.info("LIST1 : " + list1);
		JavaRDD<Tuple2<String, Integer>> y1 = lines.map(e -> new Tuple2<String, Integer>(e, e.length()));
		List<Tuple2<String, Integer>> list2 = y1.collect();
		logger.info("LIST2 : " + list2);
	}

	public static void main(String[] args) {
		MapExample ex = new MapExample();
		ex.test();
	}
}
