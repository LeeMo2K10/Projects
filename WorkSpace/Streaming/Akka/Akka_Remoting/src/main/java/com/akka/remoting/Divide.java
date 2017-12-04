package com.akka.remoting;


public class Divide implements MathOp{

	 private static final long serialVersionUID = 1L;
	    private final double n1;
	    private final int n2;

	    public Divide(double n1, int n2) {
	      this.n1 = n1;
	      this.n2 = n2;
	    }

	    public double getN1() {
	      return n1;
	    }

	    public int getN2() {
	      return n2;
	    }
	
}