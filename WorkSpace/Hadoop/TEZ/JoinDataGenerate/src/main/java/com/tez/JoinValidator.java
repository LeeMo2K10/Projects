package com.tez;

import java.io.IOException;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.util.ToolRunner;
import org.apache.tez.client.TezClient;
import org.apache.tez.common.counters.TezCounter;
import org.apache.tez.dag.api.DAG;
import org.apache.tez.dag.api.Edge;
import org.apache.tez.dag.api.ProcessorDescriptor;
import org.apache.tez.dag.api.TezConfiguration;
import org.apache.tez.dag.api.Vertex;
import org.apache.tez.dag.api.Vertex.VertexExecutionContext;
import org.apache.tez.dag.api.client.DAGClient;
import org.apache.tez.dag.api.client.DAGStatus;
import org.apache.tez.dag.api.client.StatusGetOpts;
import org.apache.tez.mapreduce.input.MRInput;
import org.apache.tez.runtime.library.conf.OrderedPartitionedKVEdgeConfig;
import org.apache.tez.runtime.library.partitioner.HashPartitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.sun.jersey.core.impl.provider.entity.XMLJAXBElementProvider.Text;

public class JoinValidator extends TezExampleBase {
	private static final Logger LOG = LoggerFactory.getLogger(JoinValidator.class);

	private static final String LHS_INPUT_NAME = "lhsfile";
	private static final String RHS_INPUT_NAME = "rhsfile";

	private static final String COUNTER_GROUP_NAME = "JOIN_VALIDATE";
	private static final String MISSING_KEY_COUNTER_NAME = "MISSING_KEY_EXISTS";

	public static void main(String[] args) throws Exception {
		JoinValidator validate = new JoinValidator();
		int status = ToolRunner.run(new Configuration(), validate, args);
		System.exit(status);
	}

	@Override
	protected void printUsage() {
		System.err.println("Usage: " + "joinvalidate <path1> <path2>");
	}

	@Override
	protected int runJob(String[] args, TezConfiguration tezConf, TezClient tezClient) throws Exception {

		LOG.info("Running JoinValidate");

		String lhsDir = args[0];
		String rhsDir = args[1];
		int numPartitions = 1;
		if (args.length == 3) {
			numPartitions = Integer.parseInt(args[2]);
		}

		if (numPartitions <= 0) {
			System.err.println("NumPartitions must be > 0");
			return 4;
		}

		Path lhsPath = new Path(lhsDir);
		Path rhsPath = new Path(rhsDir);

		DAG dag = createDag(tezConf, lhsPath, rhsPath, numPartitions);

		tezClient.waitTillReady();
		DAGClient dagClient = tezClient.submitDAG(dag);
		Set<StatusGetOpts> getOpts = Sets.newHashSet();
		if (isCountersLog()) {
			getOpts.add(StatusGetOpts.GET_COUNTERS);
		}
		DAGStatus dagStatus = dagClient.waitForCompletionWithStatusUpdates(getOpts);
		if (dagStatus.getState() != DAGStatus.State.SUCCEEDED) {
			LOG.info("DAG diagnostics: " + dagStatus.getDiagnostics());
			return -1;
		} else {
			dagStatus = dagClient.getDAGStatus(Sets.newHashSet(StatusGetOpts.GET_COUNTERS));
			TezCounter counter = dagStatus.getDAGCounters().findCounter(COUNTER_GROUP_NAME, MISSING_KEY_COUNTER_NAME);
			if (counter == null) {
				LOG.info("Unable to determing equality");
				return -2;
			} else {
				if (counter.getValue() != 0) {
					LOG.info("Validate failed. The two sides are not equivalent");
					return -3;
				} else {
					LOG.info("Validation successful. The two sides are equivalent");
					return 0;
				}
			}
		}

	}

	@Override
	protected int validateArgs(String[] otherArgs) {
		if (otherArgs.length != 3 && otherArgs.length != 2) {
			return 2;
		}
		return 0;
	}

	@VisibleForTesting
	DAG createDag(TezConfiguration tezConf, Path lhs, Path rhs, int numPartitions) throws IOException {
		DAG dag = DAG.create(getDagName());
		if (getDefaultExecutionContext() != null) {
			dag.setExecutionContext(getDefaultExecutionContext());
		}

		OrderedPartitionedKVEdgeConfig edgeConf = OrderedPartitionedKVEdgeConfig
				.newBuilder(Text.class.getName(), NullWritable.class.getName(), HashPartitioner.class.getName())
				.setFromConfiguration(tezConf).build();

		Vertex lhsVertex = Vertex
				.create(LHS_INPUT_NAME, ProcessorDescriptor.create(ForwardingProcessor.class.getName()))
				.addDataSource("lhs",
						MRInput.createConfigBuilder(new Configuration(tezConf), TextInputFormat.class,
								lhs.toUri().toString()).groupSplits(!isDisableSplitGrouping())
								.generateSplitsInAM(!isGenerateSplitInClient()).build());
		setVertexExecutionContext(lhsVertex, getLhsExecutionContext());

		Vertex rhsVertex = Vertex
				.create(RHS_INPUT_NAME, ProcessorDescriptor.create(ForwardingProcessor.class.getName()))
				.addDataSource("rhs",
						MRInput.createConfigBuilder(new Configuration(tezConf), TextInputFormat.class,
								rhs.toUri().toString()).groupSplits(!isDisableSplitGrouping())
								.generateSplitsInAM(!isGenerateSplitInClient()).build());
		setVertexExecutionContext(rhsVertex, getRhsExecutionContext());

		Vertex joinValidateVertex = Vertex.create("joinvalidate",
				ProcessorDescriptor.create(JoinValidateProcessor.class.getName()), numPartitions);
		setVertexExecutionContext(joinValidateVertex, getValidateEvertexecutionContext());

		Edge e1 = Edge.create(lhsVertex, joinValidateVertex, edgeConf.createDefaultEdgeProperty());
		Edge e2 = Edge.create(rhsVertex, joinValidateVertex, edgeConf.createDefaultEdgeProperty());

		dag.addVertex(lhsVertex).addVertex(rhsVertex).addVertex(joinValidateVertex).addEdge(e1).addEdge(e2);
		return dag;
	}

	private void setVertexExecutionContext(Vertex vertex, VertexExecutionContext executionContext) {
		if (executionContext != null) {
			vertex.setExecutionContext(executionContext);
		}
	}

	protected VertexExecutionContext getDefaultExecutionContext() {
		return null;
	}

	protected VertexExecutionContext getLhsExecutionContext() {
		return null;
	}

	protected VertexExecutionContext getRhsExecutionContext() {
		return null;
	}

	protected VertexExecutionContext getValidateEvertexecutionContext() {
		return null;
	}

	protected String getDagName() {
		return "JoinValidate";
	}
}
