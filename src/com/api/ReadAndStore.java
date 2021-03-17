package com.api;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.Callable;

public class ReadAndStore implements Callable<Long> {
	static File OUTPUT_LOCATION = new File("output");

	private TestURL test;
	private int runId;

	ReadAndStore (TestURL test, int runId) {
		this.test = test;
		this.runId = runId;
	}

	public int getRunId() {
		return this.runId;
	}
	
	@Override
	public Long call() throws Exception {
		File testOutput = new File(OUTPUT_LOCATION, test.getId());
		if (testOutput.exists()) {
			testOutput.delete();
			testOutput.mkdir();
		}
		String url = test.getUrl().toString()+"?runId="+String.format("%02d",runId)+"&timestamp="+System.currentTimeMillis();
		System.out.println(url+" RUN");
		long l = System.currentTimeMillis();
		URL nocacheUrl = new URL(url);	
		try (FileOutputStream fileOutputStream = new FileOutputStream(new File(testOutput, test.getId()+"_"+runId));
				FileChannel fileChannel = fileOutputStream.getChannel();
				ReadableByteChannel readableByteChannel = Channels.newChannel(nocacheUrl.openStream());) {
			fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
		}
		catch(java.net.SocketException e) {
			e.printStackTrace();
		}
		long res = (System.currentTimeMillis()-l);
		System.out.println(url+" END");
		return res;
	}
}
