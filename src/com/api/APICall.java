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
	static final int NB_TRIAL = 25;
	
	public static void main(String[] args) throws Exception {

		TestURL testShort1 = new TestURL("6KB-BACKEND", new URL("https://witty-wave-08c1e3003.azurestaticapps.net/api/short"));
		TestURL testShort2 = new TestURL("6KB-API-APIGEE", new URL("http://axa-mvp-entity-test.apigee.net/ghertest/short"));
		TestURL testShort3 = new TestURL("6KB-API-AZURE_EU_NORTH", new URL("https://gherapi.azure-api.net/short"));
		TestURL testShort4 = new TestURL("6KB-API-AZURE_EU_WEST", new URL("https://gherapiwest.azure-api.net/short"));

		TestURL testMessage1 = new TestURL("2MB-BACKEND", new URL("https://witty-wave-08c1e3003.azurestaticapps.net/api/message"));
		TestURL testMessage2 = new TestURL("2MB-API-APIGEE", new URL("http://axa-mvp-entity-test.apigee.net/ghertest/message"));
		TestURL testMessage3 = new TestURL("2MB-API-AZURE_EU_NORTH", new URL("https://gherapi.azure-api.net/message"));
		TestURL testMessage4 = new TestURL("2MB-API-AZURE_EU_WEST", new URL("https://gherapiwest.azure-api.net/message"));

		TestURL testBig1 = new TestURL("8MB-BACKEND", new URL("https://witty-wave-08c1e3003.azurestaticapps.net/api/big"));
		TestURL testBig2 = new TestURL("8MB-API-APIGEE", new URL("http://axa-mvp-entity-test.apigee.net/ghertest/big"));
		TestURL testBig3 = new TestURL("8MB-API-AZURE_EU_NORTH", new URL("https://gherapi.azure-api.net/big"));
		TestURL testBig4 = new TestURL("8MB-API-AZURE_EU_WEST", new URL("https://gherapiwest.azure-api.net/big"));

		/** real-run **/
		runTest(testShort1, NB_TRIAL);
		runTest(testShort2, NB_TRIAL);
		runTest(testShort3, NB_TRIAL);
		runTest(testShort4, NB_TRIAL);

		/** results **/
		System.out.println("---------------");
		System.out.println(testShort1.getPrintableResult());
		System.out.println(testShort2.getPrintableResult());
		System.out.println(testShort3.getPrintableResult());
		System.out.println(testShort4.getPrintableResult());
		System.out.println("---------------");
		
		exporCSV("testShort.csv", testShort1, testShort2, testShort3, testShort4);
		exporSummaryCSV("testShortSummary.csv", testShort1, testShort2, testShort3, testShort4);

		/** real-run **/
		runTest(testMessage1, NB_TRIAL);
		runTest(testMessage2, NB_TRIAL);
		runTest(testMessage3, NB_TRIAL);
		runTest(testMessage4, NB_TRIAL);

		/** results **/
		System.out.println("---------------");
		System.out.println(testMessage1.getPrintableResult());
		System.out.println(testMessage2.getPrintableResult());
		System.out.println(testMessage3.getPrintableResult());
		System.out.println(testMessage4.getPrintableResult());
		System.out.println("---------------");
		
		exporCSV("testMessage.csv", testMessage1, testMessage2, testMessage3, testMessage4);
		exporSummaryCSV("testMessageSummary.csv", testMessage1, testMessage2, testMessage3, testMessage4);

		/** real-run **/
		runTest(testBig1, NB_TRIAL);
		runTest(testBig2, NB_TRIAL);
		runTest(testBig3, NB_TRIAL);
		runTest(testBig4, NB_TRIAL);

		// /** results **/
		System.out.println("---------------");
		System.out.println(testBig1.getPrintableResult());
		System.out.println(testBig2.getPrintableResult());
		System.out.println(testBig3.getPrintableResult());
		System.out.println(testBig4.getPrintableResult());
		System.out.println("---------------");
		
		exporCSV("testBig.csv", testBig1, testBig2, testBig3, testBig4);
		exporSummaryCSV("testBigSummary.csv", testBig1, testBig2, testBig3, testBig4);
	}

	static void runTest(TestURL test, int nbtrials) throws Exception {
		long[] c = new long[nbtrials];
		for (int i=0; i<nbtrials; i++) {
			// c[i] = readAndStore(test, i);
			c[i] = readAndForget(test, i);
		}
		test.setResults(c);
		System.out.println(test);
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
	
	static void exporCSV(String filename, TestURL ... testURLs) throws Exception {
		String DEL = ";";

		try (FileOutputStream fileOutputStream = new FileOutputStream(
				new File(OUTPUT_LOCATION, filename))) {

			int trials = 0;
			String header = "Trial ID"+DEL;
			for (TestURL testUrl : testURLs) {
				long[] testUrls = testUrl.getResults();
				trials = testUrls.length;
				header += testUrl.getId()+DEL;
			}
			header += "\n";
			fileOutputStream.write(header.getBytes());

			String s = "";
			for (int i=0; i<trials; i++) {
				s += "T"+(i+1)+DEL;
				for (TestURL testUrl : testURLs) {
					s += testUrl.getResults()[i]+DEL;
				}
				s += "\n";
			}
			fileOutputStream.write(s.getBytes());
		}
	}
	
	static void exporSummaryCSV(String filename, TestURL ... testURLs) throws Exception {
		String DEL = ";";

		try (FileOutputStream fileOutputStream = new FileOutputStream(
				new File(OUTPUT_LOCATION, filename))) {

			String header = "ID"+DEL+"CNT"+DEL+"SUM"+DEL+"MIN"+DEL+"MAX"+DEL+"AVG"+DEL+"MED"+DEL+"STD"+"\n";
			fileOutputStream.write(header.getBytes());
			
			for (TestURL t : testURLs) {
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
