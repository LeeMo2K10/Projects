package com.graph.max;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.graphx.Graph;
import org.apache.spark.graphx.GraphLoader;
import org.apache.spark.storage.StorageLevel;

public class BuildingEdgesFromFile implements Serializable {

	private static final long serialVersionUID = 1L;
	static Logger log = Logger.getLogger(BuildingEdgesFromFile.class);

	public void build() throws InterruptedException {

		SparkConf conf = new SparkConf().setMaster("local").setAppName("PropertyGraph");
		SparkContext jssc = new SparkContext(conf);

		Graph<Object, Object> graph = GraphLoader.edgeListFile(jssc, "src/main/resources/edges.txt", true, -1,
				StorageLevel.MEMORY_AND_DISK_2(), StorageLevel.MEMORY_AND_DISK_2());

		log.info(graph.vertices().toJavaRDD().collect());

		log.info(graph.ops().numVertices());
		log.info(graph.ops().numEdges());
	}

	public static void main(String[] args) throws InterruptedException {
		BuildingEdgesFromFile pr = new BuildingEdgesFromFile();
		pr.build();
	}

}
