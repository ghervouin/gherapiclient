package com.api;

import java.io.IOException;

interface OutputInterface extends AutoCloseable {
	public void write(byte b[]) throws IOException;
}
