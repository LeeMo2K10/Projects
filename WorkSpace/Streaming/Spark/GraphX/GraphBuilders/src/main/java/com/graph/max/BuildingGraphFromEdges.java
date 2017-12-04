package com.graph.max;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.graphx.Edge;
import org.apache.spark.graphx.Graph;
import org.apache.spark.graphx.GraphLoader;
import org.apache.spark.storage.StorageLevel;

import scala.reflect.ClassTag$;

public class BuildingGraphFromEdges implements Serializable {

	private static final long serialVersionUID = 1L;
	static Logger log = Logger.getLogger(BuildingGraphFromEdges.class);

	@SuppressWarnings("unchecked")
	public void get() throws InterruptedException {

		SparkConf conf = new SparkConf().setMaster("local").setAppName("PropertyGraph");
		JavaSparkContext jssc = new JavaSparkContext(conf);

		List<Edge> edgeList = new ArrayList<Edge>();
		edgeList.add(new Edge(1, 2, 4));
		edgeList.add(new Edge(1, 4, 6));
		edgeList.add(new Edge(2, 4, 8));
		edgeList.add(new Edge(3, 1, 10));
		edgeList.add(new Edge(3, 4, 12));

		JavaRDD edgeRdd = jssc.parallelize(edgeList);

		Integer defaultuser = 5;

		Graph<Integer, Integer> graph = Graph.fromEdges(edgeRdd.rdd(), defaultuser, StorageLevel.MEMORY_AND_DISK(),
				StorageLevel.MEMORY_AND_DISK(), ClassTag$.MODULE$.apply(Integer.class),
				ClassTag$.MODULE$.apply(Integer.class));

		log.info(graph.vertices().toJavaRDD().collect());

		log.info(graph.ops().numVertices());
		log.info(graph.ops().numEdges());
	}

	public static void main(String[] args) throws InterruptedException {
		BuildingGraphFromEdges pr = new BuildingGraphFromEdges();
		pr.get();
	}

}