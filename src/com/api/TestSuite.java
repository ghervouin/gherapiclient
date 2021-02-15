package com.api;

public class TestSuite {
	private String id;
	private TestURL[] tests;

	TestSuite(String id) {
		this.id = id;
	}

	TestSuite(String id, TestURL ... tests) {
		this.id = id;
		this.tests = tests;
	}

	public String getId() {
		return this.id;
	}
	
	public  TestURL[] getTests() {
		return tests;
	}

	@Override
	public String toString() {
		String s = this.getClass().getSimpleName()+"["+getId()+"]\n";
		s += tests;
		return s;
	}
}
