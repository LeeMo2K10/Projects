package com.tez.examples;

import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.tez.mapreduce.processor.SimpleMRProcessor;
import org.apache.tez.runtime.api.LogicalInput;
import org.apache.tez.runtime.api.LogicalOutput;
import org.apache.tez.runtime.api.ProcessorContext;
import org.apache.tez.runtime.api.Reader;
import org.apache.tez.runtime.library.api.KeyValueReader;
import org.apache.tez.runtime.library.api.KeyValueWriter;

import com.google.common.base.Preconditions;

public class HashJoinProcessor extends SimpleMRProcessor {
	private static final String streamingSide = "streamingSide";
	private static final String hashSide = "hashSide";
	private static final String joinOutput = "joinOutput";

	public HashJoinProcessor(ProcessorContext context) {
		super(context);

	}

	@Override
	public void run() throws Exception {
		Preconditions.checkState(getInputs().size() == 2);
		Preconditions.checkState(getOutputs().size() == 1);
		LogicalInput streamInput = getInputs().get(streamingSide);
		LogicalInput hashInput = getInputs().get(hashSide);
		Reader rawStreamReader = streamInput.getReader();
		Reader rawHashReader = hashInput.getReader();
		Preconditions.checkState(rawStreamReader instanceof KeyValueReader);
		Preconditions.checkState(rawHashReader instanceof KeyValueReader);
		LogicalOutput lo = getOutputs().get(joinOutput);
		Preconditions.checkState(lo.getWriter() instanceof KeyValueWriter);
		KeyValueWriter writer = (KeyValueWriter) lo.getWriter();

		KeyValueReader hashKvReader = (KeyValueReader) rawHashReader;
		Set<Text> keySet = new HashSet<Text>();
		while (hashKvReader.next()) {
			keySet.add(new Text((Text) hashKvReader.getCurrentKey()));
		}
		KeyValueReader streamKvReader = (KeyValueReader) rawStreamReader;
		while (streamKvReader.next()) {
			Text key = (Text) streamKvReader.getCurrentKey();
			if (keySet.contains(key)) {
				writer.write(key, NullWritable.get());
			}
		}

	}

}