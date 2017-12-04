package com.HCataLog;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hive.hcatalog.data.DefaultHCatRecord;
import org.apache.hive.hcatalog.data.HCatRecord;

public class Reduce extends Reducer<IntWritable, IntWritable, WritableComparable, HCatRecord> {

	protected void reduce(IntWritable key, Iterable<IntWritable> values,
			Reducer<IntWritable, IntWritable, WritableComparable, HCatRecord>.Context context)
			throws IOException, InterruptedException {
		int sum = 0;
		Iterator<IntWritable> itr = values.iterator();
		while (itr.hasNext()) {
			sum++;
			itr.next();
		}
		HCatRecord record = new DefaultHCatRecord(2);
		record.set(0, key.get());
		record.set(1, sum);
		context.write(null, record);

	}

}
