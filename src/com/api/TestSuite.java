package com.api;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class TestSuite {
	private String id;
	private List<TestURL> tests = new LinkedList<TestURL>();

	TestSuite(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}
	
	public void addTest(String id, URL url) {
		tests.add(new TestURL(id, url));
	}
	
	public  List<TestURL> getTests() {
		return tests;
	}

	@Override
	public String toString() {
		String s = this.getClass().getSimpleName()+"["+getId()+"]\n";
		s += tests;
		return s;
	}
}
