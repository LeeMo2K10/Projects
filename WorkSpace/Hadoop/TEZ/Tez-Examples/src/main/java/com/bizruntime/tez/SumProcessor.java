package com.bizruntime.tez;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.tez.mapreduce.processor.SimpleMRProcessor;
import org.apache.tez.runtime.api.ProcessorContext;
import org.apache.tez.runtime.library.api.KeyValueWriter;
import org.apache.tez.runtime.library.api.KeyValuesReader;

import com.google.common.base.Preconditions;

public final class SumProcessor extends SimpleMRProcessor {

	static String OUTPUT = "Output";
	static String TOKENIZER = "Tokenizer";

	public SumProcessor(ProcessorContext context) {
		super(context);
	}

	@Override
	public void run() throws Exception {
		Preconditions.checkArgument(getInputs().size() == 1);
		Preconditions.checkArgument(getOutputs().size() == 1);
		KeyValueWriter kvWriter = (KeyValueWriter) getOutputs().get(OUTPUT).getWriter();
		KeyValuesReader kvReader = (KeyValuesReader) getInputs().get(TOKENIZER).getReader();
		while (kvReader.next()) {
			Text word = (Text) kvReader.getCurrentKey();
			int sum = 0;
			for (Object value : kvReader.getCurrentValues()) {
				sum += ((IntWritable) value).get();
			}
			kvWriter.write(word, new IntWritable(sum));
		}

	}
}
