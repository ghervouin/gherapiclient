package com.api;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

import com.api.TestURL.TestStatistics;

import java.nio.ByteBuffer;

public class APICall {

	static final File OUTPUT_LOCATION = new File("output");
	static final int NB_TRIAL = 20;
	
	public static void main(String[] args) throws Exception {

		if (OUTPUT_LOCATION.exists()) {
			OUTPUT_LOCATION.delete();
		}
		OUTPUT_LOCATION.mkdir();

		TestSuite testSmall = new TestSuite("Small-6KB",
			new TestURL("BACKEND", new URL("https://witty-wave-08c1e3003.azurestaticapps.net/api/short")),
			new TestURL("APIGEE_EU", new URL("http://axa-mvp-entity-test.apigee.net/ghertest/short")),
			new TestURL("AZ_EU_NORTH", new URL("https://gherapi.azure-api.net/short")),
			new TestURL("AZ_EU_WEST", new URL("https://gherapiwest.azure-api.net/short"))
		);
		
		TestSuite testMedium = new TestSuite("Medium-2MB",
			new TestURL("BACKEND", new URL("https://witty-wave-08c1e3003.azurestaticapps.net/api/message")),
			new TestURL("APIGEE_EU", new URL("http://axa-mvp-entity-test.apigee.net/ghertest/message")),
			new TestURL("AZ_EU_NORTH", new URL("https://gherapi.azure-api.net/message")),
			new TestURL("AZ_EU_WEST", new URL("https://gherapiwest.azure-api.net/message"))
		);

		TestSuite testLarge = new TestSuite("Large-8MB",
			new TestURL("BACKEND", new URL("https://witty-wave-08c1e3003.azurestaticapps.net/api/big")),
			new TestURL("APIGEE_EU", new URL("http://axa-mvp-entity-test.apigee.net/ghertest/big")),
			new TestURL("AZ_EU_NORTH", new URL("https://gherapi.azure-api.net/big")),
			new TestURL("AZ_EU_WEST", new URL("https://gherapiwest.azure-api.net/big"))
		);

		runTest(testSmall, NB_TRIAL);
		System.out.println(testSmall);
		exporCSV(testSmall.getId()+".csv", testSmall);
		exporSummaryCSV(testSmall.getId()+"-Summary.csv", testSmall);
		
		runTest(testMedium, NB_TRIAL);
		System.out.println(testMedium);
		exporCSV(testMedium.getId()+".csv", testMedium);
		exporSummaryCSV(testMedium.getId()+"-Summary.csv", testMedium);
		
		runTest(testLarge, NB_TRIAL);
		System.out.println(testLarge);				
		exporCSV(testLarge.getId()+".csv", testLarge);
		exporSummaryCSV(testLarge.getId()+"-Summary.csv", testLarge);
	}

	static void runTest(TestSuite testSuite, int nbTrials) throws Exception {
		for (int i=0; i<nbTrials; i++) {
			for (TestURL test : testSuite.getTests()) {
				// test.addResult(readAndStore(test, i));
				test.addResult(readAndForget(test, i));
			}
		}
	}

	static long readAndStore(TestURL test, int runId) throws Exception {
		File testOutput = new File(OUTPUT_LOCATION, test.getId());
		if (testOutput.exists()) {
			testOutput.delete();
			testOutput.mkdir();
		}
		long l = System.currentTimeMillis();
		URL nocacheUrl = new URL(test.getUrl().toString()+"?timestamp="+l);	
		try (FileOutputStream fileOutputStream = new FileOutputStream(new File(testOutput, test.getId()+"_"+runId));
				FileChannel fileChannel = fileOutputStream.getChannel();
				ReadableByteChannel readableByteChannel = Channels.newChannel(nocacheUrl.openStream());) {
			fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
		}
		catch(java.net.SocketException e) {
			System.err.println("Socket exception: " + e);
		}
		return (System.currentTimeMillis()-l);
	}

	static long readAndForget(TestURL test, int runId) throws Exception {
		long l = System.currentTimeMillis();
		URL nocacheUrl = new URL(test.getUrl().toString()+"?timestamp="+l);	
		try (ReadableByteChannel readableByteChannel = Channels.newChannel(nocacheUrl.openStream())) {
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			while (readableByteChannel.read(buffer) != -1) {
				buffer.clear();
			}
		}
		catch(java.net.SocketException e) {
			System.err.println("Socket exception: " + e);
		}
		return (System.currentTimeMillis()-l);
	}

	static void exporCSV(String filename, TestSuite testSuite) throws Exception {
		String DEL = ";";

		try (FileOutputStream fileOutputStream = new FileOutputStream(
				new File(OUTPUT_LOCATION, filename))) {

			int trials = 0;
			String header = "Trial ID"+DEL;
			for (TestURL testUrl: testSuite.getTests()) {
				trials = testUrl.getResults().size();
				header += testUrl.getId()+DEL;
			}
			header += "\n";
			fileOutputStream.write(header.getBytes());

			String s = "";
			for (int i=0; i<trials; i++) {
				s += "T"+(i+1)+DEL;
				for (TestURL testUrl: testSuite.getTests()) {
					s += testUrl.getResult(i)+DEL;
				}
				s += "\n";
			}
			fileOutputStream.write(s.getBytes());
		}
	}

	static void exporSummaryCSV(String filename, TestSuite testSuite) throws Exception {
		String DEL = ";";

		try (FileOutputStream fileOutputStream = new FileOutputStream(
				new File(OUTPUT_LOCATION, filename))) {

			String header = "ID"+DEL+"CNT"+DEL+"SUM"+DEL+"MIN"+DEL+"MAX"+DEL+"AVG"+DEL+"MED"+DEL+"STD"+"\n";
			fileOutputStream.write(header.getBytes());

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
				fileOutputStream.write(s.getBytes());
			}
			
		}
	}
}
