package com.api;

import java.util.Arrays;

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
		s += Arrays.toString(tests);
		return s;
	}
}
