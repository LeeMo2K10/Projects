package com.akka.distribute;

import java.io.Serializable;

public class Get implements Serializable {

	private static final long serialVersionUID = 1L;
	public long counterId;

	public Get(long counterId) {
		this.counterId = counterId;
	}
}
