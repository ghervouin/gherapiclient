package com.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

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
			
			APICallRun run = new APICallRun(ts);
			run.execute();
			System.out.println(ts);

			String runTimestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			try (Output outputCSV = new FileOutput(
					OUTPUT_LOCATION +"\\"+ts.getId()+"-"+runTimestamp+".csv")) {
				run.exporCSV(outputCSV, ts);
			}

			try (Output outputSummaryCSV = new FileOutput(
					OUTPUT_LOCATION +"\\"+ts.getId()+"-"+runTimestamp+"-summary.csv")) {
				run.exporSummaryCSV(outputSummaryCSV, ts);
			}
		}

	}

	class AzureBlobOutput implements Output {
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

	class FileOutput implements Output {
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
}
