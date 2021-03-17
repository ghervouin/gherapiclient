package com.api;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.api.TestURL.TestStatistics;

public class APICallRun {

	private TestSuite testSuite;

	public APICallRun(TestSuite testSuite) {
		this.testSuite = testSuite;
	}
	
	public void execute() throws Exception {
		if (testSuite.isParallel()) executeParallel();
		else executeNonParallel();
	}
	
	private void executeNonParallel() throws Exception {
		for (int i=0; i<testSuite.getNbTrials(); i++) {
			for (TestURL test : testSuite.getTests()) {
				System.out.println(String.format("From test suite %s, testing %s on url %s", testSuite.getId(), test.getId(), test.getUrl()));
				// ReadAndStore r = new ReadAndStore(test, i);
				ReadAndForget r = new ReadAndForget(test, i);
				test.addResult(r.call());
			}
		}
	}

	private void executeParallel() throws Exception {
		for (TestURL test : testSuite.getTests()) {
			List<Callable<Long>> tasks = Collections.synchronizedList(new LinkedList<Callable<Long>>());
			for (int i=0; i<testSuite.getNbTrials(); i++) {
				tasks.add(new ReadAndForget(test, i));
			}
			// ExecutorService exec = Executors.newCachedThreadPool();
	        ExecutorService exec = Executors.newFixedThreadPool(testSuite.getNbTrials());
	        try {
	            List<Future<Long>> results = exec.invokeAll(tasks);
	            for (Future<Long> fr : results) {
	            	test.addResult(fr.get());
	            }
	        } finally {
	            exec.shutdown();
	        }
	    }
	}

	public void exporCSV(Output output, TestSuite testSuite) throws Exception {
		String DEL = ";";

		int trials = 0;
		String header = "Trial ID"+DEL;
		for (TestURL testUrl: testSuite.getTests()) {
			trials = testUrl.getResults().size();
			header += testUrl.getId()+DEL;
		}
		header += "\n";
		output.write(header.getBytes());

		String s = "";
		for (int i=0; i<trials; i++) {
			s += "T"+(i+1)+DEL;
			for (TestURL testUrl: testSuite.getTests()) {
				s += testUrl.getResult(i)+DEL;
			}
			s += "\n";
		}
		output.write(s.getBytes());
	}

    public void exporSummaryCSV(Output output, TestSuite testSuite) throws Exception {
		String DEL = ";";

		String header = "ID"+DEL+"CNT"+DEL+"SUM"+DEL+"MIN"+DEL+"MAX"+DEL+"AVG"+DEL+"MED"+DEL+"STD"+"\n";
		output.write(header.getBytes());

		for (TestURL t : testSuite.getTests()) {
			TestStatistics ts = t.getTestStatistics();
			String s = "";
			s += t.getId()+DEL;
			s += ts.getCount()+DEL;
			s += ts.getSum()+DEL;
			s += ts.getMin()+DEL;
			s += ts.getMax()+DEL;
			s += ts.getAverage()+DEL;
			s += ts.getMedian()+DEL;
			s += ts.getStderr()+DEL;
			s += "\n";
			output.write(s.getBytes());
		}
	}
}
