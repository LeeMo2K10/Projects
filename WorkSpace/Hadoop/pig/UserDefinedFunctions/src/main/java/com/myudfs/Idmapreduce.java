package com.myudfs;

import java.io.IOException;

import org.apache.pig.PigServer;

public class Idmapreduce {
	public static void main(String[] args) {
		try {
			PigServer pigServer = new PigServer("mapreduce");
			runIdQuery(pigServer, "passwd");
		} catch (Exception e) {
		}
	}

	public static void runIdQuery(PigServer pigServer, String inputFile) throws IOException {
		pigServer.registerQuery("A = load '/pigdata/user.txt ' using PigStorage(':');");
		pigServer.registerQuery("B = foreach A generate $0 as uid;");
		pigServer.store("B", "/user/idout.txt");
	}
}