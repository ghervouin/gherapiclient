package com.api;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class APICall {

	static File OUTPUT_LOCATION = new File("output");
	public static void main(String[] args) throws Exception {

		TestURL testShort1 = new TestURL("6KB-BACK", new URL("https://witty-wave-08c1e3003.azurestaticapps.net/api/short"));
		TestURL testShort2 = new TestURL("6KB-GAPI", new URL("http://axa-mvp-entity-test.apigee.net/ghertest/short"));
		TestURL testShort3 = new TestURL("6KB-AAPI", new URL("https://gherapi.azure-api.net/short"));
		TestURL testShort4 = new TestURL("6KB-AAPI", new URL("https://gherapiwest.azure-api.net/short"));

		TestURL testMessage1 = new TestURL("2MB-BACK", new URL("https://witty-wave-08c1e3003.azurestaticapps.net/api/message"));
		TestURL testMessage2 = new TestURL("2MB-GAPI", new URL("http://axa-mvp-entity-test.apigee.net/ghertest/message"));
		TestURL testMessage3 = new TestURL("2MB-AAPI", new URL("https://gherapi.azure-api.net/message"));
		TestURL testMessage4 = new TestURL("2MB-AAPI", new URL("https://gherapiwest.azure-api.net/message"));

		TestURL testBig1 = new TestURL("8MB-BACK", new URL("https://witty-wave-08c1e3003.azurestaticapps.net/api/big"));
		TestURL testBig2 = new TestURL("8MB-GAPI", new URL("http://axa-mvp-entity-test.apigee.net/ghertest/big"));
		TestURL testBig3 = new TestURL("8MB-AAPI", new URL("https://gherapi.azure-api.net/big"));
		TestURL testBig4 = new TestURL("8MB-AAPI", new URL("https://gherapiwest.azure-api.net/big"));

		/** dry-run **/
		runTest(testShort1, 4);
		runTest(testShort2, 4);
		runTest(testShort3, 4);
		runTest(testShort4, 4);
		
		/** real-run **/
		runTest(testShort1, 20);
		runTest(testShort2, 20);
		runTest(testShort3, 20);
		runTest(testShort4, 20);
		
		/** results **/
		System.out.println("---------------");
		System.out.println(testShort1.getPrintableResult());
		System.out.println(testShort2.getPrintableResult());
		System.out.println(testShort3.getPrintableResult());
		System.out.println(testShort4.getPrintableResult());
		System.out.println("---------------");
		
		/** dry-run **/
		runTest(testMessage1, 4);
		runTest(testMessage2, 4);
		runTest(testMessage3, 4);
		runTest(testMessage4, 4);
		
		/** real-run **/
		runTest(testMessage1, 20);
		runTest(testMessage2, 20);
		runTest(testMessage3, 20);
		runTest(testMessage4, 20);
		
		/** results **/
		System.out.println("---------------");
		System.out.println(testMessage1.getPrintableResult());
		System.out.println(testMessage2.getPrintableResult());
		System.out.println(testMessage3.getPrintableResult());
		System.out.println(testMessage4.getPrintableResult());
		System.out.println("---------------");
		
		/** dry-run **/
		runTest(testBig1, 4);
		runTest(testBig2, 4);
		runTest(testBig3, 4);
		runTest(testBig4, 4);
		
		/** real-run **/
		runTest(testBig1, 20);
		runTest(testBig2, 20);
		runTest(testBig3, 20);
		runTest(testBig4, 20);
		
		/** results **/
		System.out.println("---------------");
		System.out.println(testBig1.getPrintableResult());
		System.out.println(testBig2.getPrintableResult());
		System.out.println(testBig3.getPrintableResult());
		System.out.println(testBig4.getPrintableResult());
		System.out.println("---------------");
	}

	static void runTest(TestURL test, int nbtrials) throws Exception {
		File testOutput = new File(OUTPUT_LOCATION, test.id);
		testOutput.delete();
		testOutput.mkdir();

		long[] c = new long[nbtrials];
		for (int i=0; i<nbtrials; i++) c[i] = readAndStore(test, i, testOutput);

		test.results = c;
		System.out.println(test);
	}

	static long readAndStore(TestURL test, int runId, File outputFolder) throws Exception {
		long l = System.currentTimeMillis();		
		ReadableByteChannel readableByteChannel = Channels.newChannel(test.url.openStream());
		try (FileOutputStream fileOutputStream = new FileOutputStream(new File(outputFolder, test.id+"_"+runId));
				FileChannel fileChannel = fileOutputStream.getChannel()) {
			fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
		}
		return (System.currentTimeMillis()-l);
	}
}
