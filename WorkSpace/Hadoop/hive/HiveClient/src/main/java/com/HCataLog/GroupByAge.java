package com.HCataLog;

import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
/*import org.apache.hcatalog.common.HCatConstants;
import org.apache.hcatalog.data.DefaultHCatRecord;
import org.apache.hcatalog.data.schema.HCatSchema;
import org.apache.hcatalog.mapreduce.HCatInputFormat;
import org.apache.hcatalog.mapreduce.HCatOutputFormat;
import org.apache.hcatalog.mapreduce.InputJobInfo;
import org.apache.hcatalog.mapreduce.OutputJobInfo;*/
import org.apache.hive.hcatalog.common.HCatConstants;
import org.apache.hive.hcatalog.data.DefaultHCatRecord;
import org.apache.hive.hcatalog.data.schema.HCatSchema;
import org.apache.hive.hcatalog.mapreduce.HCatInputFormat;
import org.apache.hive.hcatalog.mapreduce.HCatOutputFormat;
import org.apache.hive.hcatalog.mapreduce.InputJobInfo;
import org.apache.hive.hcatalog.mapreduce.OutputJobInfo;

public class GroupByAge extends Configured implements Tool {

	@SuppressWarnings({ "unused", "deprecation" })
	public int run(String[] args) throws Exception {
		Configuration conf = getConf();
		args = new GenericOptionsParser(conf, args).getRemainingArgs();

		String inputTableName = args[0];
		String outputTableName = args[1];
		String dbName = "bizruntime";
		String principalID = System.getProperty(HCatConstants.HCAT_METASTORE_PRINCIPAL);
		if (principalID != null)
			conf.set(HCatConstants.HCAT_METASTORE_PRINCIPAL, principalID);
		Job job = new Job(conf, "GroupByAge");
		HCatInputFormat.setInput(job, dbName, inputTableName);
		job.setInputFormatClass(HCatInputFormat.class);
		job.setJarByClass(GroupByAge.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setOutputKeyClass(WritableComparable.class);
		job.setOutputValueClass(DefaultHCatRecord.class);

		HCatOutputFormat.setOutput(job, OutputJobInfo.create(dbName, outputTableName, null));
		HCatSchema s = HCatOutputFormat.getTableSchema(conf);
		System.err.println("INFO: output schema explicitly set for writing:" + s);
		HCatOutputFormat.setSchema(job, s);
		job.setOutputFormatClass(HCatOutputFormat.class);
		return (job.waitForCompletion(true) ? 0 : 1);

	}

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new GroupByAge(), args);
		System.exit(exitCode);
	}

}