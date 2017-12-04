package com.Spark;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

public class FilterExample {
	static Logger logger = Logger.getLogger(FilterExample.class);

	public void test() {
		SparkConf conf = new SparkConf().setAppName("FilterExample").setMaster("spark://master:7077");
		JavaSparkContext sc = new JavaSparkContext(conf);
		Function<Integer, Boolean> filterPredicate = e -> e % 2 == 0;
		JavaRDD<Integer> rddX = sc.parallelize(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 2);
		JavaRDD<Integer> rddY = rddX.filter(filterPredicate);
		List<Integer> filteredList = rddY.collect();

		logger.info("List Of Filter Elements : " + filteredList);

	}

	public static void main(String[] args) {
		FilterExample ex = new FilterExample();
		ex.test();
	}
}
