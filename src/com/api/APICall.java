package com.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.api.TestURL.TestStatistics;

public class APICall {

	static File OUTPUT_LOCATION = new File("output");
	static int nbTrials = 20;
	static boolean PARALLEL = false;

	public static void main(String[] args) throws Exception {
		
		TestSuite testSmall = new TestSuite("Small-6KB", nbTrials, PARALLEL,
			new TestURL("BACKEND", new URL("https://witty-wave-08c1e3003.azurestaticapps.net/api/short")),
			new TestURL("APIGEE_EU", new URL("http://axa-mvp-entity-test.apigee.net/ghertest/short")),
			new TestURL("AZ_EU_NORTH", new URL("https://gherapi.azure-api.net/short")),
			new TestURL("AZ_EU_WEST", new URL("https://gherapiwest.azure-api.net/short")),
			new TestURL("ESG_SEDC", new URL("https://api-int.se.axa-go.axa.com/api/short"))
		);
		
		TestSuite testMedium = new TestSuite("Medium-2MB", nbTrials, PARALLEL,
			new TestURL("BACKEND", new URL("https://witty-wave-08c1e3003.azurestaticapps.net/api/message")),
			new TestURL("APIGEE_EU", new URL("http://axa-mvp-entity-test.apigee.net/ghertest/message")),
			new TestURL("AZ_EU_NORTH", new URL("https://gherapi.azure-api.net/message")),
			new TestURL("AZ_EU_WEST", new URL("https://gherapiwest.azure-api.net/message")),
			new TestURL("ESG_SEDC", new URL("https://api-int.se.axa-go.axa.com/api/message"))
		);

		TestSuite testLarge = new TestSuite("Large-8MB", nbTrials, PARALLEL,
			new TestURL("BACKEND", new URL("https://witty-wave-08c1e3003.azurestaticapps.net/api/big")),
			new TestURL("APIGEE_EU", new URL("http://axa-mvp-entity-test.apigee.net/ghertest/big")),
			new TestURL("AZ_EU_NORTH", new URL("https://gherapi.azure-api.net/big")),
			new TestURL("AZ_EU_WEST", new URL("https://gherapiwest.azure-api.net/big")),
			new TestURL("ESG_SEDC", new URL("https://api-int.se.axa-go.axa.com/api/big"))
		);

		new APICall(testSmall, testMedium, testLarge);
	}

	public APICall (TestSuite ... testSuites) throws Exception {
		for (TestSuite ts : testSuites) {
			System.out.println("wave ----");
			
			String runTimestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			runTest(ts);
			System.out.println(ts);
			try (FileOutput foCSV = new FileOutput(
					OUTPUT_LOCATION +"\\"+ts.getId()+"-"+runTimestamp+".csv")) {
				exporCSV(foCSV, ts);
			}
			try (FileOutput foSummaryCSV = new FileOutput(
					OUTPUT_LOCATION +"\\"+ts.getId()+"-"+runTimestamp+".csv")) {
				exporSummaryCSV(foSummaryCSV, ts);
			}
		}
	}

	class AzureBlobOutput implements OutputInterface {
		// private FileOutputStream fileOutputStream;
		AzureBlobOutput(String location) throws Exception {
		}
		@Override
		public void write(byte[] b) throws IOException {
			// TODO implement writer
		}
		@Override
		public void close() throws IOException {
			// TODO close IO stream
		}
	}

	class FileOutput implements OutputInterface {
		private FileOutputStream fileOutputStream;
		FileOutput(String filelocation) throws Exception {
			this.fileOutputStream = new FileOutputStream(
				new File(filelocation));
		}
		@Override
		public void write(byte[] b) throws IOException {
			fileOutputStream.write(b);
		}
		@Override
		public void close() throws IOException {
			if (fileOutputStream != null)
				fileOutputStream.close();
		}
	}

	static void runTest(TestSuite testSuite) throws Exception {
		if (testSuite.isParallel()) runTestParallel(testSuite);
		else runTestNonParallel(testSuite);
	}
	
	static void runTestNonParallel(TestSuite testSuite) throws Exception {
		for (int i=0; i<testSuite.getNbTrials(); i++) {
			for (TestURL test : testSuite.getTests()) {
				// ReadAndStore r = new ReadAndStore(test, i);
				ReadAndForget r = new ReadAndForget(test, i);
				test.addResult(r.call());
			}
		}
	}

	static void runTestParallel(TestSuite testSuite) throws Exception {
		for (TestURL test : testSuite.getTests()) {
			List<Callable<Long>> tasks = new LinkedList<Callable<Long>>();
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

	void exporCSV(OutputInterface output, TestSuite testSuite) throws Exception {
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

    void exporSummaryCSV(OutputInterface output, TestSuite testSuite) throws Exception {
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
