package com.akka.router;

import java.util.*;

import akka.actor.UntypedActor;

public class StatsWorker extends UntypedActor {
	Map<String, Integer> cache = new HashMap<String, Integer>();

	@Override
	public void onReceive(Object message) throws Exception {

		if (message instanceof String) {
			String word = (String) message;
			Integer length = cache.get(word);
			if (length == null) {
				length = word.length();
				cache.put(word, length);
			}
			getSender().tell(length, getSelf());

		} else {
			unhandled(message);
		}
	}
}
