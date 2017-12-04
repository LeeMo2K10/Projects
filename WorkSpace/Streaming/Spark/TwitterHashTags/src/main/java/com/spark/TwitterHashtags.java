package com.spark;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.spark.streaming.twitter.TwitterUtils;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import scala.Tuple2;
import twitter4j.Status;

public class TwitterHashtags {
	static Logger logger = Logger.getLogger(TwitterHashtags.class);

	@SuppressWarnings("serial")
	public static void main(String[] args) throws IOException {
		if (args.length < 4) {
			logger.error("Usage: JavaTwitterHashTagJoinSentiments <consumer key> <consumer secret>"
					+ " <access token> <access token secret> [<filters>]");
			System.exit(1);
		}

		// StreamingExamples.setStreamingLogLevels();

		String consumerKey = args[0];
		String consumerSecret = args[1];
		String accessToken = args[2];
		String accessTokenSecret = args[3];
		String[] filters = Arrays.copyOfRange(args, 4, args.length);

		// Set the system properties so that Twitter4j library used by Twitter
		// stream
		// can use them to generate OAuth credentials
		System.setProperty("twitter4j.oauth.consumerKey", consumerKey);
		System.setProperty("twitter4j.oauth.consumerSecret", consumerSecret);
		System.setProperty("twitter4j.oauth.accessToken", accessToken);
		System.setProperty("twitter4j.oauth.accessTokenSecret",
				accessTokenSecret);

		SparkConf sparkConf = new SparkConf().setMaster("local[1]").setAppName(
				"TwitterHashtags");
		JavaStreamingContext jssc = new JavaStreamingContext(sparkConf,
				new Duration(2000));
		JavaReceiverInputDStream<Status> stream = TwitterUtils.createStream(
				jssc, filters);

		JavaDStream<String> words = stream
				.flatMap(new FlatMapFunction<Status, String>() {
					public Iterable<String> call(Status s) {
						return Arrays.asList(s.getText().split(" "));
					}
				});

		JavaDStream<String> hashTags = words
				.filter(new Function<String, Boolean>() {
					public Boolean call(String word) throws Exception {
						return word.startsWith("#");
					}
				});

		// Read in the word-sentiment list and create a static RDD from it
		String wordSentimentFilePath = "AFINN-111.txt";
		final JavaPairRDD<String, Double> wordSentiments = jssc.sparkContext()
				.textFile(wordSentimentFilePath)
				.mapToPair(new PairFunction<String, String, Double>() {
					public Tuple2<String, Double> call(String line) {
						String[] columns = line.split("\t");
						return new Tuple2<String, Double>(columns[0], Double
								.parseDouble(columns[1]));
					}
				});

		JavaPairDStream<String, Integer> hashTagCount = hashTags
				.mapToPair(new PairFunction<String, String, Integer>() {
					public Tuple2<String, Integer> call(String s) {
						// leave out the # character
						return new Tuple2<String, Integer>(s.substring(1), 1);
					}
				});

		JavaPairDStream<String, Integer> hashTagTotals = hashTagCount
				.reduceByKeyAndWindow(
						new Function2<Integer, Integer, Integer>() {
							public Integer call(Integer a, Integer b) {
								return a + b;
							}
						}, new Duration(10000));

		// Determine the hash tags with the highest sentiment values by joining
		// the streaming RDD
		// with the static RDD inside the transform() method and then
		// multiplying
		// the frequency of the hash tag by its sentiment value
		JavaPairDStream<String, Tuple2<Double, Integer>> joinedTuples = hashTagTotals
				.transformToPair(new Function<JavaPairRDD<String, Integer>, JavaPairRDD<String, Tuple2<Double, Integer>>>() {
					public JavaPairRDD<String, Tuple2<Double, Integer>> call(
							JavaPairRDD<String, Integer> topicCount)
							throws Exception {
						return wordSentiments.join(topicCount);
					}
				});

		JavaPairDStream<String, Double> topicHappiness = joinedTuples
				.mapToPair(new PairFunction<Tuple2<String, Tuple2<Double, Integer>>, String, Double>() {
					public Tuple2<String, Double> call(
							Tuple2<String, Tuple2<Double, Integer>> topicAndTuplePair)
							throws Exception {
						Tuple2<Double, Integer> happinessAndCount = topicAndTuplePair
								._2();
						return new Tuple2<String, Double>(topicAndTuplePair
								._1(), happinessAndCount._1()
								* happinessAndCount._2());
					}
				});

		JavaPairDStream<Double, String> happinessTopicPairs = topicHappiness
				.mapToPair(new PairFunction<Tuple2<String, Double>, Double, String>() {
					public Tuple2<Double, String> call(
							Tuple2<String, Double> topicHappiness)
							throws Exception {
						return new Tuple2<Double, String>(topicHappiness._2(),
								topicHappiness._1());
					}
				});

		JavaPairDStream<Double, String> happiest10 = happinessTopicPairs
				.transformToPair(new Function<JavaPairRDD<Double, String>, JavaPairRDD<Double, String>>() {
			z		public JavaPairRDD<Double, String> call(
							JavaPairRDD<Double, String> happinessAndTopics)
							throws Exception {
						return happinessAndTopics.sortByKey(false);
					}
				});

		// Print hash tags with the most positive sentiment values
		happiest10
				.foreachRDD(new Function<JavaPairRDD<Double, String>, Void>() {
					public Void call(
							JavaPairRDD<Double, String> happinessTopicPairs)
							throws Exception {
						List<Tuple2<Double, String>> topList = happinessTopicPairs
								.take(10);
						logger.debug(String
								.format("\nHappiest topics in last 10 seconds (%s total):",
										happinessTopicPairs.count()));
						for (Tuple2<Double, String> pair : topList) {
							logger.debug(String.format("%s (%s happiness)",
									pair._2(), pair._1()));
						}
						return null;
					}
				});

		jssc.start();
		jssc.awaitTermination();
	}
}