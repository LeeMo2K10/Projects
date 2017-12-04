package com.Spark;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

public class LinesCount1 {
	static Logger logger = Logger.getLogger(LinesCount1.class);

	public void test() throws InterruptedException {
		SparkConf conf = new SparkConf().setAppName("Sum").setMaster("spark://master:7077");
		JavaSparkContext sc = new JavaSparkContext(conf);

		JavaRDD<String> lines = sc.textFile("inputs/Status.txt");
		JavaPairRDD<String, Integer> pairs = lines.mapToPair(s -> new Tuple2(s, 1));
		JavaPairRDD<String, Integer> counts = pairs.reduceByKey((a, b) -> a + b);
		counts.sortByKey();
		logger.info("First :" + pairs.first());
		logger.info("Ascending Order :" + pairs.sortByKey(true));
		logger.info("Count : " + pairs.count());
		pairs.saveAsTextFile("output/Linecount");
		pairs.saveAsObjectFile("output/Linecount");
		List<Tuple2<String, Integer>> dataset = counts.collect();
		logger.info((dataset));
		Thread.sleep(1000000);

	}

	public static void main(String[] args) throws InterruptedException {
		LinesCount1 num = new LinesCount1();
		num.test();
	}

}
