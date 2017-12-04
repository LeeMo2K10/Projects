package com.oozie.coordinator;

import java.io.File;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.GregorianCalendar;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class GetDirInfo {
	private static final String OOZIE_ACTION_OUTPUT_PROPERTIES = "oozie.action.output.properties";

	public static void main(String[] args) throws Exception {
		String dirPath = args[0];
		String propKey0 = "dir.num-files";
		String propVal0 = "-1";
		String propKey1 = "dir.age";
		String propVal1 = "-1";
		System.out.println("Directory path: '" + dirPath + "'");

		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		Path hadoopDir = new Path(dirPath);
		if (fs.exists(hadoopDir)) {
			FileStatus[] files = FileSystem.get(conf).listStatus(hadoopDir);
			int numFilesInDir = files.length;
			propVal0 = Integer.toString(numFilesInDir);
			long timePassed, daysPassedLong;
			int daysPassed;
			String dirName = hadoopDir.getName();
			String[] dirNameArray = dirName.split("-");
			if (dirNameArray.length == 3) {
				int year = Integer.valueOf(dirNameArray[0]);
				int month = Integer.valueOf(dirNameArray[1]) - 1;
				int date = Integer.valueOf(dirNameArray[2]);
				GregorianCalendar dirCreationDate = new GregorianCalendar(year, month, date);
				timePassed = (new GregorianCalendar()).getTimeInMillis() - dirCreationDate.getTimeInMillis();
				daysPassed = (int) timePassed / 1000 / 60 / 60 / 24;
				;
				propVal1 = Integer.toString(daysPassed);
			}
		}
		String oozieProp = System.getProperty(OOZIE_ACTION_OUTPUT_PROPERTIES);
		if (oozieProp != null) {
			File propFile = new File(oozieProp);
			Properties props = new Properties();
			props.setProperty(propKey0, propVal0);
			props.setProperty(propKey1, propVal1);
			props.setProperty("jobTracker", "192.168.1.146:8032");
			props.setProperty("nameNode", "hdfs://localhost:9000");
			props.setProperty("queueName", "default");
			props.setProperty("oozie.libpath", "hdfs://localhost:9000/user/bizruntime/share/lib/");
			props.setProperty("oozie.use.system.libpath", "true");
			props.setProperty("oozie.wf.rerun.failnodes", "true");
		
			OutputStream os = new FileOutputStream(propFile);
			props.store(os, "");
			os.close();
		} else
			throw new RuntimeException(OOZIE_ACTION_OUTPUT_PROPERTIES + " System property not defined");
	}
}