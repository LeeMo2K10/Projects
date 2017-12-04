package com.spark.graphx;

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
import org.apache.spark.graphx.VertexRDD;
import org.apache.spark.storage.StorageLevel;

import scala.Predef;
import scala.Tuple2;
import scala.math.Ordered;
import scala.math.Ordering;
import scala.math.Ordering$;
import scala.reflect.ClassTag$;

public class UsingGraph implements Serializable {

	private static final long serialVersionUID = 1L;

	static Logger log = Logger.getLogger(UsingGraph.class);

	SparkConf conf = new SparkConf().setMaster("local").setAppName("PropertyGraph");
	JavaSparkContext jssc = new JavaSparkContext(conf);

	@SuppressWarnings("unchecked")
	private void getComponents() {

		SparkContext sc = jssc.sc();
		List<Tuple2<Object, String>> vertexlist = new ArrayList<Tuple2<Object, String>>();
		vertexlist.add(new Tuple2<Object, String>(1L, "BarackObama,Barack Obama"));
		vertexlist.add(new Tuple2<Object, String>(2L, "ladygaga,Goddess of Love"));

		vertexlist.add(new Tuple2<Object, String>(3L, "jeresig,John Resig"));

		vertexlist.add(new Tuple2<Object, String>(4l, "justinbieber,Justin Bieber"));

		vertexlist.add(new Tuple2<Object, String>(6L, "matei_zaharia,Matei Zaharia"));

		vertexlist.add(new Tuple2<Object, String>(7l, "odersky,Martin Odersky"));

		vertexlist.add(new Tuple2<Object, String>(8L, "anonsys"));

		JavaRDD vertexRdd = jssc.parallelize(vertexlist);

		List edgeList = new ArrayList();
		edgeList.add(new Edge(2l, 1l, 22));
		edgeList.add(new Edge(4l, 1l, 25));
		edgeList.add(new Edge(1l, 2l, 43));
		edgeList.add(new Edge(6l, 3l, 35));
		edgeList.add(new Edge(7l, 3l, 35));
		edgeList.add(new Edge(7l, 6l, 43));
		edgeList.add(new Edge(6l, 7l, 35));
		edgeList.add(new Edge(3l, 7l, 35));

		JavaRDD edgeRdd = jssc.parallelize(edgeList);

		Integer defaultuser = 11;

		Graph<String, Integer> graph = Graph.apply(vertexRdd.rdd(), edgeRdd.rdd(), defaultuser,
				StorageLevel.MEMORY_ONLY(), StorageLevel.MEMORY_ONLY(), ClassTag$.MODULE$.apply(String.class),
				ClassTag$.MODULE$.apply(Integer.class));
		
		Graph<String, Integer> subgraph = graph.subgraph(new EdgeFunction2(), new VertexFunction2());

		Graph<Object, Object> pageRankGraph = subgraph.ops().pageRank(0.001, 0.15);

		VertexRDD<Object> pageRdd = pageRankGraph.vertices();

		Graph<String, Integer> userInfoWithPageRank = subgraph.outerJoinVertices(pageRdd, null,
				ClassTag$.MODULE$.apply(String.class), ClassTag$.MODULE$.apply(String.class),
				Predef.$eq$colon$eq$.MODULE$.<String>tpEquals());

		//userInfoWithPageRank.vertices().top(5, Ordering$.MODULE$.by(new OrderFunction2(), Ordered.class));
	}

	public static void main(String[] args) {

		UsingGraph app = new UsingGraph();
		app.getComponents();
	}
}
