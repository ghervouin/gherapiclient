package com.api;

import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.Callable;

public class ReadAndForget implements Callable<Long> {
	private TestURL test;

	ReadAndForget (TestURL test, int runId) {
		this.test = test;
	}

	@Override
	public Long call() throws Exception {
		long l = System.currentTimeMillis();
		URL nocacheUrl = new URL(test.getUrl().toString()+"?timestamp="+l);	
		try (ReadableByteChannel readableByteChannel = Channels.newChannel(nocacheUrl.openStream())) {
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			while (readableByteChannel.read(buffer) != -1) {
				buffer.clear();
			}
		}
		catch(java.net.SocketException e) {
			e.printStackTrace();
		}
		return (System.currentTimeMillis()-l);
	}
}
