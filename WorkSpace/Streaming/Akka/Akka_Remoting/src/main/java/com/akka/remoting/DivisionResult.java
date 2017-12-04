package com.akka.remoting;


public class DivisionResult implements MathResult {
	
	private static final long serialVersionUID = 1L;
	private final double n1;
	private final int n2;
	private final double result;

	public DivisionResult(double n1, int n2, double result) {
		this.n1 = n1;
		this.n2 = n2;
		this.result = result;
	}

	public double getN1() {
		return n1;
	}

	public int getN2() {
		return n2;
	}

	public double getResult() {
		return result;
	}

}