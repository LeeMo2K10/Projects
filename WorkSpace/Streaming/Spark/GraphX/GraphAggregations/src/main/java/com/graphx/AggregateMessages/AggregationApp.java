package com.graphx.AggregateMessages;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.graphx.Graph;
import org.apache.spark.graphx.TripletFields;
import org.apache.spark.graphx.VertexRDD;
import org.apache.spark.graphx.util.GraphGenerators;

import scala.Tuple2;
import scala.reflect.ClassTag$;

public class AggregationApp implements Serializable {

	private static final long serialVersionUID = 1L;
	static Logger log = Logger.getLogger(AggregationApp.class);

	public void test() throws InterruptedException {

		int numVertices = 5;
		int numEParts = 1;
		double mu = 4.0;
		double sigma = 1.3;
		long seed = -1;

		SparkConf conf = new SparkConf().setMaster("local").setAppName("PropertyGraph");
		SparkContext jssc = new SparkContext(conf);

		Graph<Object, Object> graph = GraphGenerators.logNormalGraph(jssc, numVertices, numEParts, mu, sigma, seed);

		VertexRDD<Tuple2<Integer, Long>> olderFollowers = graph.aggregateMessages(new SendMessage(), new MergeMessage(),
				TripletFields.All, ClassTag$.MODULE$.apply(Tuple2.class));

		olderFollowers.toJavaRDD().foreach(e -> {
			System.out.println(" olderFollowers-> id : " + e._1 + "\t attr: " + Arrays.asList(e._2));
		});

		graph.vertices().toJavaRDD().foreach(f -> {
			System.out.println("Vid : " + f._1 + " VerticesAttr-->" + Arrays.asList(f._2));
		});

		graph.edges().toJavaRDD().foreach(f -> {
			System.out.println("-->" + Arrays.asList(f) + " srcID " + f + " destId " + f.dstId());
		});

		VertexRDD<Double> avgAgeOfOlderFollowers = olderFollowers.mapValues(new CountFunction(),
				ClassTag$.MODULE$.apply(Double.class));

		avgAgeOfOlderFollowers.toJavaRDD().foreach(e -> {
			System.out.println(" avgAgeOfOlderFollowers -> id : " + e._1 + "\t Average Age : " + e._2);
		});

		
		
		
	}

	public static void main(String[] args) throws InterruptedException {
		AggregationApp pr = new AggregationApp();
		pr.test();
	}

}