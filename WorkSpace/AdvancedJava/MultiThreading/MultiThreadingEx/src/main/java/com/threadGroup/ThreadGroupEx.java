package com.threadGroup;

import org.apache.log4j.Logger;

public class ThreadGroupEx implements Runnable {
	static Logger logger = Logger.getLogger(ThreadGroupEx.class);

	public void test() throws InterruptedException {
		ThreadGroup pgroup = new ThreadGroup("parent");
		logger.info("Parent GroupName : " + pgroup.getName());
		ThreadGroup cGroup = new ThreadGroup(pgroup, "Child");
		logger.info("Child GroupName : " + cGroup.getName());

		Thread th1 = new Thread(pgroup, this);
		logger.info("Thread1 Name : " + th1.getName());
		th1.start();
		Thread th2 = new Thread(cGroup, this);
		logger.info("Thread2 Name : " + th2.getName());
		th2.start();

		logger.debug("Active Count : " + pgroup.activeCount() + "  GroupName:  " + pgroup.getName());

		Thread[] list = new Thread[pgroup.activeCount()];
		int count = pgroup.enumerate(list);
		for (int i = 0; i < count; i++) {
			logger.info("Thread " + list[i].getName() + " Found");
		}
		logger.debug("Active GroupCount : " + pgroup.activeGroupCount() + "  GroupName:  " + pgroup.getName());
		pgroup.checkAccess();
		logger.info(pgroup.getName() + " Has Access ");
		int i = pgroup.getMaxPriority();
		logger.info("Max Priority Of Parent Group = " + i);
		th1.join();
		th2.join();
		cGroup.destroy();
		logger.info(cGroup.getName() + "  was Destroyed");
		pgroup.destroy();
		logger.info(pgroup.getName() + "  was Destroyed");

	}

	public void run() {
		for (int i = 0; i < 1000; i++) {
			i++;
		}
		logger.info("Thread Name : " + Thread.currentThread().getName());
	}

	public static void main(String[] args) throws InterruptedException {
		ThreadGroupEx te = new ThreadGroupEx();
		te.test();
	}

}