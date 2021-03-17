package com.api;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.Callable;

public class ReadAndForget implements Callable<Long> {
	private TestURL test;
	private int runId;

	ReadAndForget (TestURL test, int runId) {
		this.test = test;
		this.runId = runId;
	}

	public int getRunId() {
		return this.runId;
	}

	@Override
	public Long call() throws Exception {
		String url = test.getUrl().toString()+"?runId="+String.format("%02d",runId)+"&timestamp="+System.currentTimeMillis();
		System.out.println(url+" RUN");
		long l = System.currentTimeMillis();
		URL nocacheUrl = new URL(url);	
		try (ReadableByteChannel readableByteChannel = Channels.newChannel(nocacheUrl.openStream())) {
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			while (readableByteChannel.read(buffer) != -1) {
				buffer.clear();
			}
		}
		catch(java.net.SocketException e) {
			e.printStackTrace();
		}
		long res = (System.currentTimeMillis()-l);
		System.out.println(url+" END");
		return res;
	}
}
