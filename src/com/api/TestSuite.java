package com.api;

import java.util.Arrays;

public class TestSuite {
	private String id;
	private int nbTrials;
	private int nbParallel;
	private TestURL[] tests;

	TestSuite(String id, int nbTrials, int nbParallel, TestURL ... tests) {
		assert(nbTrials >= 5);
		this.id = id;
		this.nbTrials = nbTrials;
		this.nbParallel = nbParallel;
		this.tests = tests;
	}

	public String getId() {
		return this.id;
	}
	
	public TestURL[] getTests() {
		return tests;
	}

	public int getNbTrials() {
		return this.nbTrials;
	}

	public int getNbParallel() {
		return this.nbParallel;
	}

	@Override
	public String toString() {
		String s = this.getClass().getSimpleName()+"["+getId()+"]\n";
		s += Arrays.toString(tests);
		return s;
	}
}
