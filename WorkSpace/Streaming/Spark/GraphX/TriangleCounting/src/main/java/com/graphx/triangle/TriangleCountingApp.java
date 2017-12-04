package com.graphx.triangle;

import java.io.Serializable;

import org.apache.log4j.Logger;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.graphx.Graph;
import org.apache.spark.graphx.GraphLoader;
import org.apache.spark.graphx.VertexRDD;
import org.apache.spark.storage.StorageLevel;

import scala.Tuple2;

public class TriangleCountingApp implements Serializable {

	private static final long serialVersionUID = 1L;

	static Logger log = Logger.getLogger(TriangleCountingApp.class);

	SparkConf conf = new SparkConf().setMaster("local").setAppName("PropertyGraph");
	SparkContext jssc = new SparkContext(conf);

	private void getComponents() {

		Graph<Object, Object> graph = GraphLoader.edgeListFile(jssc, "src/main/resources/Followers.txt", true, -1,
				StorageLevel.MEMORY_AND_DISK(), StorageLevel.MEMORY_AND_DISK());

		graph.vertices().toJavaRDD().foreach(f -> {
			log.info("" + f);
		});
		graph.edges().toJavaRDD().foreach(f -> {
			log.info("" + f);
		});
		VertexRDD<Object> connectedCompo = graph.ops().triangleCount().vertices();

		log.info(connectedCompo.toJavaRDD().collect());
		JavaPairRDD<Object, Object> rankrdd = connectedCompo.toJavaRDD().mapToPair(f -> {

			return new Tuple2<Object, Object>(f._1, f._2);

		});

		JavaPairRDD<Object, Object> users = jssc.textFile("src/main/resources/users.txt", 2).toJavaRDD()
				.mapToPair(lines -> {

					String[] fields = lines.split(",");

					return new Tuple2<Object, Object>(Long.parseLong(fields[0]), fields[1]);
				});

		JavaRDD<Object> usersByRank = users.join(rankrdd).map(f -> {

			return f;
		});
		log.info(usersByRank.collect());

	}

	public static void main(String[] args) {

		TriangleCountingApp app = new TriangleCountingApp();
		app.getComponents();
	}
}
