package com.ThreadLocal;

import org.apache.log4j.Logger;

import com.threadGroup.ThreadGroupEx;

public class ThreadLocalEx extends Thread {
	static Logger logger = Logger.getLogger(ThreadGroupEx.class);

	private static ThreadLocal t = new ThreadLocal() {
		protected Object initialValue() {
			return new Integer(n--);
		}
	};

	private static int n = 10;

	ThreadLocalEx(String name) {
		super(name);

	}
}