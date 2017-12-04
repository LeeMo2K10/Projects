package com.Spark;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

public class GroupByExample {
	static Logger logger = Logger.getLogger(FilterExample.class);

	public void test() {
		SparkConf conf = new SparkConf().setAppName("GroupByExample").setMaster("spark://master:7077");
		JavaSparkContext sc = new JavaSparkContext(conf);
		JavaRDD<String> rddX = sc.parallelize(
				Arrays.asList("Joseph", "Jimmy", "Tina", "Thomas", "James", "Cory", "Christine", "Jackeline", "Juan"),
				3);

		JavaPairRDD<Character, Iterable<String>> rddY = rddX.groupBy(word -> word.charAt(0));

		List<Tuple2<Character, Iterable<String>>> list = rddY.collect();
		logger.info("List Of Grouping Elements : " + list);

	}

	public static void main(String[] args) {
		GroupByExample ex = new GroupByExample();
		ex.test();
	}
}
