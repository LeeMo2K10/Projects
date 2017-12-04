package com.tez.example;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.ToolRunner;
import org.apache.tez.client.TezClient;
import org.apache.tez.dag.api.DAG;
import org.apache.tez.dag.api.DataSinkDescriptor;
import org.apache.tez.dag.api.DataSourceDescriptor;
import org.apache.tez.dag.api.Edge;
import org.apache.tez.dag.api.ProcessorDescriptor;
import org.apache.tez.dag.api.TezConfiguration;
import org.apache.tez.dag.api.Vertex;
import org.apache.tez.mapreduce.input.MRInput;
import org.apache.tez.mapreduce.output.MROutput;
import org.apache.tez.runtime.library.conf.OrderedPartitionedKVEdgeConfig;
import org.apache.tez.runtime.library.partitioner.HashPartitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderedWordCount extends TezExampleBase {
	private static String INPUT = WordCount.INPUT;
	private static String OUTPUT = WordCount.OUTPUT;
	private static String TOKENIZER = WordCount.TOKENIZER;
	private static String SUMMATION = WordCount.SUMMATION;
	private static String SORTER = "Sorter";
	private static final Logger LOG = LoggerFactory.getLogger(OrderedWordCount.class);

	public static DAG createDAG(TezConfiguration tezConf, String inputPath, String outputPath, int numPartitions,
			boolean disableSplitGrouping, boolean isGenerateSplitInClient, String dagName) throws IOException {

		DataSourceDescriptor dataSource = MRInput
				.createConfigBuilder(new Configuration(tezConf), TextInputFormat.class, inputPath)
				.groupSplits(!disableSplitGrouping).generateSplitsInAM(!isGenerateSplitInClient).build();

		DataSinkDescriptor dataSink = MROutput
				.createConfigBuilder(new Configuration(tezConf), TextOutputFormat.class, outputPath).build();

		Vertex tokenizerVertex = Vertex.create(TOKENIZER, ProcessorDescriptor.create(TokenProcessor.class.getName()));
		tokenizerVertex.addDataSource(INPUT, dataSource);
		OrderedPartitionedKVEdgeConfig summationEdgeConf = OrderedPartitionedKVEdgeConfig
				.newBuilder(Text.class.getName(), IntWritable.class.getName(), HashPartitioner.class.getName())
				.setFromConfiguration(tezConf).build();

		Vertex summationVertex = Vertex.create(SUMMATION, ProcessorDescriptor.create(SumProcessor.class.getName()),
				numPartitions);

		OrderedPartitionedKVEdgeConfig sorterEdgeConf = OrderedPartitionedKVEdgeConfig
				.newBuilder(IntWritable.class.getName(), Text.class.getName(), HashPartitioner.class.getName())
				.setFromConfiguration(tezConf).build();
		Vertex sorterVertex = Vertex.create(SORTER, ProcessorDescriptor.create(SorterProcessor.class.getName()), 1);
		sorterVertex.addDataSink(OUTPUT, dataSink);
		DAG dag = DAG.create(dagName);
		dag.addVertex(tokenizerVertex).addVertex(summationVertex).addVertex(sorterVertex)
				.addEdge(Edge.create(tokenizerVertex, summationVertex, summationEdgeConf.createDefaultEdgeProperty()))
				.addEdge(Edge.create(summationVertex, sorterVertex, sorterEdgeConf.createDefaultEdgeProperty()));
		return dag;
	}

	@Override
	protected void printUsage() {
		System.err.println("Usage: " + " orderedwordcount in out [numPartitions]");
	}

	@Override
	protected int validateArgs(String[] otherArgs) {
		if (otherArgs.length < 2 || otherArgs.length > 3) {
			return 2;
		}
		return 0;
	}

	@Override
	protected int runJob(String[] args, TezConfiguration tezConf, TezClient tezClient) throws Exception {
		DAG dag = createDAG(tezConf, args[0], args[1], args.length == 3 ? Integer.parseInt(args[2]) : 1,
				isDisableSplitGrouping(), isGenerateSplitInClient(), "OrderedWordCount");
		LOG.info("Running OrderedWordCount");
		return runDag(dag, isCountersLog(), LOG);
	}

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new OrderedWordCount(), args);
		System.exit(res);
	}
}