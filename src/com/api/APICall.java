package com.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class APICall {

	static File OUTPUT_LOCATION = new File("output");
	static int NB_TRIALS = 20;
	static int NB_PARALLEL = 1;

	private static void deleteFolder() {

		if (OUTPUT_LOCATION.exists()) {
			try {
				Files.newDirectoryStream(OUTPUT_LOCATION.toPath()).forEach((Path p) -> {if (p.toFile().isFile()) p.toFile().delete();});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else OUTPUT_LOCATION.mkdir();
	}
	
	public static void main(String[] args) throws Exception {

		// deleteFolder();

		int nbTrials = (args != null && args.length > 0) ? Integer.valueOf(args[0]) : NB_TRIALS;
		int nbParallel = (args != null && args.length > 1) ? Integer.valueOf(args[1]) : NB_PARALLEL;
		
		TestSuite testSmall = new TestSuite("Small-6KB", nbTrials, nbParallel,
			new TestURL("BACKEND", new URL("https://witty-wave-08c1e3003.azurestaticapps.net/api/short")),
			new TestURL("APIGEE_EU", new URL("http://axa-mvp-entity-test.apigee.net/ghertest/short")),
			new TestURL("AZ_EU_NORTH", new URL("https://gherapi.azure-api.net/short")),
			new TestURL("AZ_EU_WEST", new URL("https://gherapiwest.azure-api.net/short")),
			new TestURL("ESG_SEDC", new URL("https://api-int.se.axa-go.axa.com/api/short")));
		
		TestSuite testMedium = new TestSuite("Medium-2MB", nbTrials, nbParallel,
			new TestURL("BACKEND", new URL("https://witty-wave-08c1e3003.azurestaticapps.net/api/message")),
			new TestURL("APIGEE_EU", new URL("http://axa-mvp-entity-test.apigee.net/ghertest/message")),
			new TestURL("AZ_EU_NORTH", new URL("https://gherapi.azure-api.net/message")),
			new TestURL("AZ_EU_WEST", new URL("https://gherapiwest.azure-api.net/message")),
			new TestURL("ESG_SEDC", new URL("https://api-int.se.axa-go.axa.com/api/message")));

		TestSuite testLarge = new TestSuite("Large-8MB", nbTrials, nbParallel,
			new TestURL("BACKEND", new URL("https://witty-wave-08c1e3003.azurestaticapps.net/api/big")),
			new TestURL("APIGEE_EU", new URL("http://axa-mvp-entity-test.apigee.net/ghertest/big")),
			new TestURL("AZ_EU_NORTH", new URL("https://gherapi.azure-api.net/big")),
			new TestURL("AZ_EU_WEST", new URL("https://gherapiwest.azure-api.net/big")),
			new TestURL("ESG_SEDC", new URL("https://api-int.se.axa-go.axa.com/api/big")));

		new APICall(testSmall, testMedium, testLarge);
	}

	public APICall (TestSuite ... testSuites) throws Exception {

		for (TestSuite ts : testSuites) {
			
			APICallRun run = new APICallRun(ts);
			run.execute();
			System.out.println(ts);

			String runTimestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			String id = ts.getId()+"-"+ts.getNbTrials()+"-"+ts.getNbParallel()+"-"+runTimestamp;
			try (Output outputCSV = new FileOutput(
					OUTPUT_LOCATION +"\\"+id+".csv")) {
				run.exporCSV(outputCSV, ts);
			}

			try (Output outputSummaryCSV = new FileOutput(
					OUTPUT_LOCATION +"\\"+id+"-summary.csv")) {
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
