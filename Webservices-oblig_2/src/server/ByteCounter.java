package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class ByteCounter extends OutputStream {

	public static void main(String[] args) {
		System.out.println("double 19231902: " + getByteSize(19231902));
	}

	private long nBytes;
	private ByteCounter() {}

	// leser et serialisert objekt, og
	private static long getByteSize(Serializable o) {
		try {
			ByteCounter counter = new ByteCounter();
			ObjectOutputStream output = new ObjectOutputStream(counter);
			output.writeObject(o);
			output.close();
			return counter.getNumberOfBytes();
		} catch (Exception e) {
			return -1;
		}
	}

	private long getNumberOfBytes() {
		return nBytes;
	}

	@Override
	public void write(int b) throws IOException {
		++nBytes;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		nBytes += len;
	}
}
