package com.api;

import java.util.Arrays;

public class TestSuite {
	private String id;
	private int nbTrials;
	private boolean parallel;
	private TestURL[] tests;

	TestSuite(String id, int nbTrials, boolean parallel, TestURL ... tests) {
		this.id = id;
		this.nbTrials = nbTrials;
		this.parallel = parallel;
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

	public boolean isParallel() {
		return this.parallel;
	}

	@Override
	public String toString() {
		String s = this.getClass().getSimpleName()+"["+getId()+"]\n";
		s += Arrays.toString(tests);
		return s;
	}
}
