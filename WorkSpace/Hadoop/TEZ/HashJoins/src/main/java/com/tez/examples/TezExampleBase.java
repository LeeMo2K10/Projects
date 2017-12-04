/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tez.examples;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.cli.Options;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceAudience.Private;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.tez.client.CallerContext;
import org.apache.tez.client.TezClient;
import org.apache.tez.common.TezUtilsInternal;
import org.apache.tez.dag.api.DAG;
import org.apache.tez.dag.api.TezConfiguration;
import org.apache.tez.dag.api.TezException;
import org.apache.tez.dag.api.client.DAGClient;
import org.apache.tez.dag.api.client.DAGStatus;
import org.apache.tez.dag.api.client.StatusGetOpts;
import org.apache.tez.hadoop.shim.HadoopShim;
import org.apache.tez.hadoop.shim.HadoopShimsLoader;
import org.apache.tez.runtime.library.api.TezRuntimeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;

@InterfaceAudience.Private
public abstract class TezExampleBase extends Configured implements Tool {

	private static final Logger LOG = LoggerFactory.getLogger(TezExampleBase.class);

	private TezClient tezClientInternal;
	protected static final String DISABLE_SPLIT_GROUPING = "disableSplitGrouping";
	protected static final String LOCAL_MODE = "local";
	protected static final String COUNTER_LOG = "counter";
	protected static final String GENERATE_SPLIT_IN_CLIENT = "generateSplitInClient";

	private boolean disableSplitGrouping = false;
	private boolean isLocalMode = false;
	private boolean isCountersLog = false;
	private boolean generateSplitInClient = false;
	private HadoopShim hadoopShim;

	protected boolean isCountersLog() {
		return isCountersLog;
	}

	protected boolean isDisableSplitGrouping() {
		return disableSplitGrouping;
	}

	protected boolean isGenerateSplitInClient() {
		return generateSplitInClient;
	}

	private Options getExtraOptions() {
		Options options = new Options();
		options.addOption(LOCAL_MODE, false, "run it as local mode");
		options.addOption(DISABLE_SPLIT_GROUPING, false, "disable split grouping");
		options.addOption(COUNTER_LOG, false, "print counter log");
		options.addOption(GENERATE_SPLIT_IN_CLIENT, false, "whether generate split in client");
		return options;
	}

	public final int run(String[] args) throws Exception {
		Configuration conf = getConf();
		GenericOptionsParser optionParser = new GenericOptionsParser(conf, getExtraOptions(), args);
		String[] otherArgs = optionParser.getRemainingArgs();
		if (optionParser.getCommandLine().hasOption(LOCAL_MODE)) {
			isLocalMode = true;
		}
		if (optionParser.getCommandLine().hasOption(DISABLE_SPLIT_GROUPING)) {
			disableSplitGrouping = true;
		}
		if (optionParser.getCommandLine().hasOption(COUNTER_LOG)) {
			isCountersLog = true;
		}
		if (optionParser.getCommandLine().hasOption(GENERATE_SPLIT_IN_CLIENT)) {
			generateSplitInClient = true;
		}
		hadoopShim = new HadoopShimsLoader(conf).getHadoopShim();

		return _execute(otherArgs, null, null);
	}

	public int run(TezConfiguration conf, String[] args, @Nullable TezClient tezClient) throws Exception {
		setConf(conf);
		hadoopShim = new HadoopShimsLoader(conf).getHadoopShim();
		GenericOptionsParser optionParser = new GenericOptionsParser(conf, getExtraOptions(), args);
		if (optionParser.getCommandLine().hasOption(LOCAL_MODE)) {
			isLocalMode = true;
			if (tezClient != null) {
				throw new RuntimeException("can't specify local mode when TezClient is created, it takes no effect");
			}
		}
		if (optionParser.getCommandLine().hasOption(DISABLE_SPLIT_GROUPING)) {
			disableSplitGrouping = true;
		}
		if (optionParser.getCommandLine().hasOption(COUNTER_LOG)) {
			isCountersLog = true;
		}
		if (optionParser.getCommandLine().hasOption(GENERATE_SPLIT_IN_CLIENT)) {
			generateSplitInClient = true;
		}
		String[] otherArgs = optionParser.getRemainingArgs();
		return _execute(otherArgs, conf, tezClient);
	}

	public int runDag(DAG dag, boolean printCounters, Logger logger)
			throws TezException, InterruptedException, IOException {
		tezClientInternal.waitTillReady();

		CallerContext callerContext = CallerContext.create("TezExamples", "Tez Example DAG: " + dag.getName());
		ApplicationId appId = tezClientInternal.getAppMasterApplicationId();
		if (hadoopShim == null) {
			Configuration conf = (getConf() == null ? new Configuration(false) : getConf());
			hadoopShim = new HadoopShimsLoader(conf).getHadoopShim();
		}

		if (appId != null) {
			TezUtilsInternal.setHadoopCallerContext(hadoopShim, appId);
			callerContext.setCallerIdAndType(appId.toString(), "TezExampleApplication");
		}
		dag.setCallerContext(callerContext);

		DAGClient dagClient = tezClientInternal.submitDAG(dag);
		Set<StatusGetOpts> getOpts = Sets.newHashSet();
		if (printCounters) {
			getOpts.add(StatusGetOpts.GET_COUNTERS);
		}

		DAGStatus dagStatus;
		dagStatus = dagClient.waitForCompletionWithStatusUpdates(getOpts);

		if (dagStatus.getState() != DAGStatus.State.SUCCEEDED) {
			logger.info("DAG diagnostics: " + dagStatus.getDiagnostics());
			return -1;
		}
		return 0;
	}

	private int _validateArgs(String[] args) {
		int res = validateArgs(args);
		if (res != 0) {
			_printUsage();
			return res;
		}
		return 0;
	}

	private int _execute(String[] otherArgs, TezConfiguration tezConf, TezClient tezClient) throws Exception {

		int result = _validateArgs(otherArgs);
		if (result != 0) {
			return result;
		}

		if (tezConf == null) {
			tezConf = new TezConfiguration(getConf());
		}
		if (isLocalMode) {
			LOG.info("Running in local mode...");
			tezConf.setBoolean(TezConfiguration.TEZ_LOCAL_MODE, true);
			tezConf.set("fs.defaultFS", "file:///");
			tezConf.setBoolean(TezRuntimeConfiguration.TEZ_RUNTIME_OPTIMIZE_LOCAL_FETCH, true);
		}
		UserGroupInformation.setConfiguration(tezConf);
		boolean ownTezClient = false;
		if (tezClient == null) {
			ownTezClient = true;
			tezClientInternal = createTezClient(tezConf);
		} else {
			tezClientInternal = tezClient;
		}
		try {
			return runJob(otherArgs, tezConf, tezClientInternal);
		} finally {
			if (ownTezClient && tezClientInternal != null) {
				tezClientInternal.stop();
			}
		}
	}

	private TezClient createTezClient(TezConfiguration tezConf) throws IOException, TezException {
		TezClient tezClient = TezClient.create(getClass().getSimpleName(), tezConf);
		tezClient.start();
		return tezClient;
	}

	private void _printUsage() {
		printUsage();
		System.err.println();
		printExtraOptionsUsage(System.err);
		System.err.println();
		ToolRunner.printGenericCommandUsage(System.err);
	}
	protected abstract void printUsage();

	protected void printExtraOptionsUsage(PrintStream ps) {
		ps.println("Tez example extra options supported are");
		
		ps.println("-" + LOCAL_MODE
				+ "\t\trun it in tez local mode, currently it can only access local file system in tez local mode,"
				+ " run it in distributed mode without this option");
		ps.println("-" + DISABLE_SPLIT_GROUPING + "\t\t disable split grouping for MRInput,"
				+ " enable split grouping without this option.");
		ps.println("-" + COUNTER_LOG + "\t\t to print counters information");
		ps.println("-" + GENERATE_SPLIT_IN_CLIENT + "\t\tgenerate input split in client");
		ps.println();
		ps.println("The Tez example extra options usage syntax is ");
		ps.println("example_name [extra_options] [example_parameters]");
	}
	protected abstract int validateArgs(String[] otherArgs);
	protected abstract int runJob(String[] args, TezConfiguration tezConf, TezClient tezClient) throws Exception;

	@Private
	@VisibleForTesting
	public ApplicationId getAppId() {
		if (tezClientInternal == null) {
			LOG.warn("TezClient is not initialized, return null for AppId");
			return null;
		}
		return tezClientInternal.getAppMasterApplicationId();
	}
}
