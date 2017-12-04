package com.Spark.SparkStreamingEx;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;

public class NetworkWordCount implements Serializable {
	static Logger logger = Logger.getLogger(NetworkWordCount.class);

	@SuppressWarnings("serial")
	public void test() {
		SparkConf conf = new SparkConf().setAppName("NetworkWordCount").setMaster("local[*]");
		JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(1));

		JavaReceiverInputDStream<String> lines = jssc.socketTextStream("localhost", 9999);
		JavaDStream<String> words = lines.flatMap(new FlatMapFunction<String, String>() {

			public Iterable<String> call(String s) throws Exception {

				return Arrays.asList(s.split(" "));
			}

		});
		JavaPairDStream<String, Integer> pairs = words.mapToPair(new PairFunction<String, String, Integer>() {

			public Tuple2<String, Integer> call(String t) throws Exception {

				return new Tuple2<String, Integer>(t, 1);
			}
		});
		JavaPairDStream<String, Integer> wordsCount = pairs.reduceByKey(new Function2<Integer, Integer, Integer>() {

			public Integer call(Integer v1, Integer v2) throws Exception {

				return v1 + v2;
			}
		});
		wordsCount.print();

		logger.info("WordCount : " + wordsCount);
		jssc.start();
		jssc.awaitTermination();

	}

	public static void main(String[] args) {
		NetworkWordCount count = new NetworkWordCount();
		count.test();
	}
}