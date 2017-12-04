package com.graph.max;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.graphx.Edge;
import org.apache.spark.graphx.EdgeDirection;
import org.apache.spark.graphx.Graph;
import org.apache.spark.storage.StorageLevel;

import scala.Tuple2;
import scala.reflect.ClassTag$;

public class CollectingNeighbourId implements Serializable {

	private static final long serialVersionUID = 1L;
	static Logger log = Logger.getLogger(CollectingNeighbourId.class);

	@SuppressWarnings({ "unchecked", "resource", "rawtypes" })
	public void getId() throws InterruptedException {

		SparkConf conf = new SparkConf().setMaster("local").setAppName("PropertyGraph");
		JavaSparkContext jssc = new JavaSparkContext(conf);

		List<Tuple2<Object, Integer>> vertexlist = new ArrayList<Tuple2<Object, Integer>>();
		vertexlist.add(new Tuple2<Object, Integer>(1L, 22));
		vertexlist.add(new Tuple2<Object, Integer>(3L, 25));

		vertexlist.add(new Tuple2<Object, Integer>(5L, 43));

		vertexlist.add(new Tuple2<Object, Integer>(7L, 42));

		vertexlist.add(new Tuple2<Object, Integer>(9L, 67));

		JavaRDD<Tuple2<Object, Integer>> vertexRdd = jssc.parallelize(vertexlist);

		List<Edge> edgeList = new ArrayList<Edge>();
		edgeList.add(new Edge(3, 5, 22));
		edgeList.add(new Edge(5, 3, 25));
		edgeList.add(new Edge(1, 5, 43));
		edgeList.add(new Edge(5, 9, 35));
		edgeList.add(new Edge(7, 3, 35));

		JavaRDD edgeRdd = jssc.parallelize(edgeList);

		Integer defaultuser = 11;

		Graph<Integer, Integer> graph = Graph.apply(vertexRdd.rdd(), edgeRdd.rdd(), defaultuser,
				StorageLevel.MEMORY_ONLY(), StorageLevel.MEMORY_ONLY(), ClassTag$.MODULE$.<Integer>apply(Integer.class),
				ClassTag$.MODULE$.<String>apply(Integer.class));

		graph.ops().collectNeighborIds(EdgeDirection.Either()).toJavaRDD().foreach(f -> {
			System.out.print(f._1);
			for (Object edge : f._2) {

				log.info(" " + edge);
			}
			log.info("\n");
		});

		graph.ops().collectNeighbors(EdgeDirection.Either()).toJavaRDD().foreach(f -> {

			System.out.print(f._1);
			for (Object edge : f._2) {

				log.info(" " + edge);
			}
			log.info("\n");
		});

	}

	public static void main(String[] args) throws InterruptedException {
		CollectingNeighbourId pr = new CollectingNeighbourId();
		pr.getId();
	}

}