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
import org.apache.spark.graphx.PartitionStrategy;
import org.apache.spark.storage.StorageLevel;

import scala.Option;
import scala.Tuple2;
import scala.reflect.ClassTag$;

public class BuildingGraphFromEdgeTuples implements Serializable {

	private static final long serialVersionUID = 1L;
	static Logger log = Logger.getLogger(BuildingGraphFromEdgeTuples.class);

	@SuppressWarnings("unchecked")
	public void get() throws InterruptedException {

		SparkConf conf = new SparkConf().setMaster("local").setAppName("PropertyGraph");
		JavaSparkContext jssc = new JavaSparkContext(conf);

		List<Tuple2<Object, Object>> edgeList = new ArrayList<Tuple2<Object, Object>>();

		edgeList.add(new Tuple2<Object, Object>(1L, 2L));
		edgeList.add(new Tuple2<Object, Object>(2L, 4L));
		edgeList.add(new Tuple2<Object, Object>(3L, 1L));
		edgeList.add(new Tuple2<Object, Object>(1L, 6L));
		edgeList.add(new Tuple2<Object, Object>(3L, 4L));

		JavaRDD<Tuple2<Object, Object>> edgeRdd = jssc.parallelize(edgeList);

		Option<PartitionStrategy> uniqueEdges = Option.apply(PartitionStrategy.EdgePartition1D$.MODULE$);

		Object defaultuser = 5;

		Graph<Object, Object> graph = Graph.fromEdgeTuples(edgeRdd.rdd(), defaultuser, uniqueEdges,
				StorageLevel.MEMORY_AND_DISK(), StorageLevel.MEMORY_AND_DISK(), ClassTag$.MODULE$.apply(Object.class));

		log.info(graph.vertices().toJavaRDD().collect());

		log.info(graph.ops().numVertices());
		log.info(graph.ops().numEdges());
	}

	public static void main(String[] args) throws InterruptedException {
		BuildingGraphFromEdgeTuples pr = new BuildingGraphFromEdgeTuples();
		pr.get();
	}

}
