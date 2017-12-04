package com.graphx.edgerdd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.graphx.Edge;
import org.apache.spark.graphx.EdgeRDD;
import org.apache.spark.graphx.Graph;
import org.apache.spark.storage.StorageLevel;

import scala.Tuple2;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;

public class MapValuesApp implements Serializable {

	private static final long serialVersionUID = 1L;

	static Logger log = Logger.getLogger(MapValuesApp.class);

	@SuppressWarnings({ "unchecked", "rawtypes", "resource" })
	public void test() {

		SparkConf conf = new SparkConf().setMaster("local").setAppName("VertexRDDs");
		JavaSparkContext jssc = new JavaSparkContext(conf);

		List<Tuple2<Object, Integer>> vertexlist = new ArrayList<Tuple2<Object, Integer>>();
		vertexlist.add(new Tuple2<Object, Integer>(1L, 22));
		vertexlist.add(new Tuple2<Object, Integer>(3L, 25));

		vertexlist.add(new Tuple2<Object, Integer>(5L, 43));

		vertexlist.add(new Tuple2<Object, Integer>(7L, 42));

		vertexlist.add(new Tuple2<Object, Integer>(9L, 67));

		JavaRDD vertexRdd = jssc.parallelize(vertexlist);

		List<Edge> edgeList = new ArrayList<Edge>();
		edgeList.add(new Edge(3, 5, 4));
		edgeList.add(new Edge(5, 3, 6));
		edgeList.add(new Edge(1, 5, 2));
		edgeList.add(new Edge(5, 9, 5));
		edgeList.add(new Edge(7, 3, 6));

		JavaRDD edgeRdd = jssc.parallelize(edgeList);

		Integer defaultuser = 11;

		Graph<Integer, Integer> graph = Graph.apply(vertexRdd.rdd(), edgeRdd.rdd(), defaultuser,
				StorageLevel.MEMORY_ONLY(), StorageLevel.MEMORY_ONLY(), ClassTag$.MODULE$.<Integer>apply(Integer.class),
				ClassTag$.MODULE$.<String>apply(Integer.class));

		EdgeRDD<Integer> mapRdd = graph.edges().mapValues(new MapFunction(), ClassTag$.MODULE$.apply(Integer.class));

		mapRdd.toJavaRDD().foreach(f -> {
			log.info("After Join : " + f);
		});

	}

	public static void main(String[] args) {
		MapValuesApp app = new MapValuesApp();
		app.test();
	}
}
