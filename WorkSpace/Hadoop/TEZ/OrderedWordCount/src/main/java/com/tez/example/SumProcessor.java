package com.tez.example;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.tez.runtime.api.ProcessorContext;
import org.apache.tez.runtime.library.api.KeyValueWriter;
import org.apache.tez.runtime.library.api.KeyValuesReader;
import org.apache.tez.runtime.library.processor.SimpleProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class SumProcessor extends SimpleProcessor {
	private static String TOKENIZER = "Tokenizer";
	private static String SORTER = "Sorter";
	private static final Logger LOG = LoggerFactory.getLogger(SumProcessor.class);

	public SumProcessor(ProcessorContext context) {
		super(context);
	}

	@Override
	public void run() throws Exception {
		Preconditions.checkArgument(getInputs().size() == 1);
		Preconditions.checkArgument(getOutputs().size() == 1);
		KeyValueWriter kvWriter = (KeyValueWriter) getOutputs().get(SORTER).getWriter();
		KeyValuesReader kvReader = (KeyValuesReader) getInputs().get(TOKENIZER).getReader();
		while (kvReader.next()) {
			Text word = (Text) kvReader.getCurrentKey();
			int sum = 0;
			for (Object value : kvReader.getCurrentValues()) {
				sum += ((IntWritable) value).get();
			}
			kvWriter.write(new IntWritable(sum), word);
		}
	}
}
