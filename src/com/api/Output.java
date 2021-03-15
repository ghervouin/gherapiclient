package com.api;

import java.io.IOException;

interface Output extends AutoCloseable {
	public void write(byte b[]) throws IOException;
}
