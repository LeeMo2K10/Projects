package com.akka.publisher;

public class Reply {

	final int id;

	public Reply(int id) {

		this.id = id;
	}

	@Override
	public String toString() {
		return String.format("Reply(%s)", id);
	}

}
