package com.myudfs;

import java.io.*;
import java.util.*;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;

public class Udfcachetest extends EvalFunc<String> {

	public String exec(Tuple input) throws IOException {
		String concatResult = "";
		FileReader fr = new FileReader("./user.txt");
		BufferedReader d = new BufferedReader(fr);
		concatResult += d.readLine();
		/*
		 * fr = new FileReader("./smallfile2"); d = new BufferedReader(fr);
		 * concatResult += d.readLine();
		 */ return concatResult;
	}

	public List<String> getCacheFiles() {
		List<String> list = new ArrayList<String>(1);
		list.add("/pigdata/user.txt"); // This is hdfs file
		return list;
	}

	/*
	 * public List<String> getShipFiles() { List<String> list = new
	 * ArrayList<String>(1); list.add("/home/hadoop/pig/smallfile2"); // This
	 * local file return list; }
	 */
}