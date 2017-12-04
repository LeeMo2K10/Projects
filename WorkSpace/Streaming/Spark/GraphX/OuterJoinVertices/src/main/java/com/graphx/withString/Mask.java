package com.graphx.withString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.graphx.Edge;
import org.apache.spark.graphx.Graph;
import org.apache.spark.graphx.PartitionStrategy;
import org.apache.spark.storage.StorageLevel;

import scala.Predef;
import scala.Tuple2;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;

public class Mask implements Serializable {

	private static final long serialVersionUID = 1L;
	static Logger log = Logger.getLogger(Mask.class);

	@SuppressWarnings({ "unchecked", "rawtypes", "resource" })
	public void test() throws InterruptedException {
		SparkConf conf = new SparkConf().setMaster("local").setAppName("PropertyGraph");
		JavaSparkContext jssc = new JavaSparkContext(conf);

		List<Tuple2<Object, String[]>> vertexlist = new ArrayList<Tuple2<Object, String[]>>();
		vertexlist.add(new Tuple2<Object, String[]>(1L, new String[] { "Chinna", "Student" }));
		vertexlist.add(new Tuple2<Object, String[]>(3L, new String[] { "JP", "Employee" }));
		vertexlist.add(new Tuple2<Object, String[]>(5L, new String[] { "Richard", "Worker" }));
		vertexlist.add(new Tuple2<Object, String[]>(7L, new String[] { "Narendra", "Professional" }));

		JavaRDD vertexRdd = jssc.parallelize(vertexlist);
		List edgeList = new ArrayList<Edge>();
		edgeList.add(new Edge(3, 7, "collab"));
		edgeList.add(new Edge(5, 3, "advisor"));
		edgeList.add(new Edge(1, 5, "colleague"));
		edgeList.add(new Edge(5, 9, "Friend"));

		JavaRDD edgeRdd = jssc.parallelize(edgeList);

		String[] defaultuser = new String[] { "defaultuser", "default" };

		List<Tuple2<Object, String[]>> vertexList2 = new ArrayList<Tuple2<Object, String[]>>();
		vertexList2.add(new Tuple2<Object, String[]>(2L, new String[] { "Vijay", "Software" }));
		vertexList2.add(new Tuple2<Object, String[]>(6L, new String[] { "santanu", "BIZRUNTIME" }));

		JavaRDD<Tuple2<Object, String[]>> vertexRdd2 = jssc.parallelize(vertexList2);

		Graph<String[], String> graph = Graph.<String[], String>apply(vertexRdd.rdd(), edgeRdd.rdd(), defaultuser,
				StorageLevel.MEMORY_ONLY(), StorageLevel.MEMORY_ONLY(),
				ClassTag$.MODULE$.<String[]>apply(String[].class), ClassTag$.MODULE$.<String>apply(String.class));

		Graph<String[], String> joinGraph = graph.<String[], String[]>outerJoinVertices(vertexRdd2.rdd(),
				new VertexFunction(), ClassTag$.MODULE$.apply(String[].class), ClassTag$.MODULE$.apply(String[].class),
				Predef.$eq$colon$eq$.MODULE$.<String[]>tpEquals());

		Thread.sleep(1000);

		log.info(graph.ops().numVertices());

		log.info(joinGraph.ops().numVertices());

		joinGraph.vertices().toJavaRDD().foreach(f -> {
					System.out.println(Arrays.asList(f._2));
		});

	}

	public static void main(String[] args) throws InterruptedException {
		Mask pr = new Mask();
		pr.test();
	}

}