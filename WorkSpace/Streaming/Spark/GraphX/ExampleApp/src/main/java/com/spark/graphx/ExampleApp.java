package com.spark.graphx;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.graphx.Graph;
import org.apache.spark.graphx.GraphLoader;
import org.apache.spark.graphx.VertexRDD;
import org.apache.spark.storage.StorageLevel;

import scala.Predef;
import scala.Tuple2;
import scala.math.Ordering$;
import scala.reflect.ClassTag$;

public class ExampleApp implements Serializable {

	private static final long serialVersionUID = 1L;

	static Logger log = Logger.getLogger(ExampleApp.class);

	SparkConf conf = new SparkConf().setMaster("local").setAppName("PropertyGraph");
	SparkContext jssc = new SparkContext(conf);

	private void getComponents() {

		Graph<Object, Object> followerGraph = GraphLoader.edgeListFile(jssc, "src/main/resources/Followers.txt", true,
				-1, StorageLevel.MEMORY_AND_DISK(), StorageLevel.MEMORY_AND_DISK());

		JavaPairRDD<Object, Object> users = jssc.textFile("src/main/resources/users.txt", 2).toJavaRDD()
				.mapToPair(lines -> {

					String[] fields = lines.split(",");

					return new Tuple2<Object, Object>(Long.parseLong(fields[0]),
							Arrays.asList(fields).get(fields.length - 1));
				});

		Graph<Object, Object> graph = followerGraph.outerJoinVertices(users.rdd(), new VertexFunction1(),
				ClassTag$.MODULE$.apply(Object.class), ClassTag$.MODULE$.apply(Object.class),
				Predef.$eq$colon$eq$.MODULE$.<Object>tpEquals());

		Graph<Object, Object> subgraph = graph.subgraph(new EdgeFunction(), new VertexFunction());

		Graph<Object, Object> pageRankGraph = subgraph.ops().pageRank(0.001, 0.15);

		VertexRDD<Object> pageRdd = pageRankGraph.vertices();

		Graph<Object, Object> userInfoWithPageRank = subgraph.outerJoinVertices(pageRdd, new VertexFunction1(),
				ClassTag$.MODULE$.apply(Object.class), ClassTag$.MODULE$.apply(Object.class),
				Predef.$eq$colon$eq$.MODULE$.<Object>tpEquals());

		userInfoWithPageRank.vertices().top(5, Ordering$.MODULE$.by(new OrderFunction(), null));

	}

	public static void main(String[] args) {

		ExampleApp app = new ExampleApp();
		app.getComponents();
	}
}
