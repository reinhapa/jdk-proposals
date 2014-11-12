package net.reini.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.IntConsumer;

/**
 * Utility methods for copying data between a {@link InputStream} and
 * {@link OutputStream} or between a {@link Reader} and a {@link Writer}.
 * 
 * @author Patrick Reinhart
 * @since 1.9
 */
public final class IOUtil {
	// buffer size used for reading and writing
	private static final int BUFFER_SIZE = 8192;

	private IOUtil() {
		throw new Error("no instances");
	}

	/**
	 * Reads all bytes from an input stream and writes them to an output stream.
	 * 
	 * @param source
	 *            the input stream to read from
	 * @param target
	 *            the path to the file
	 *
	 * @return the number of bytes read and successfully written
	 *
	 * @throws IOException
	 *             if an I/O error occurs when reading or writing
	 */
	public static long copy(InputStream source, OutputStream target)
			throws IOException {
		AtomicLong totalread = new AtomicLong();
		copy(source, target, nread -> totalread.addAndGet(nread));
		return totalread.get();
	}

	/**
	 * Reads all bytes from an input stream and writes them to an output stream
	 * the given consumer on each read amount of bytes being transferred to the
	 * output stream.
	 * 
	 * @param source
	 *            the input stream to read from
	 * @param target
	 *            the path to the file
	 * @param consumer
	 *            the consumer notified about each amount of written bytes to
	 *            the target
	 *
	 * @throws IOException
	 *             if an I/O error occurs when reading or writing
	 */
	public static void copy(InputStream source, OutputStream target,
			IntConsumer consumer) throws IOException {
		byte[] buf = new byte[BUFFER_SIZE];
		int n;
		while ((n = source.read(buf)) > 0) {
			target.write(buf, 0, n);
			consumer.accept(n);
		}
	}

	/**
	 * Reads all bytes from an reader and writes them to an writer.
	 * 
	 * @param source
	 *            the input stream to read from
	 * @param target
	 *            the path to the file
	 *
	 * @return the number of characters read and successfully written
	 *
	 * @throws IOException
	 *             if an I/O error occurs when reading or writing
	 */
	public static long copy(Reader source, Writer target) throws IOException {
		AtomicLong totalread = new AtomicLong();
		copy(source, target, nread -> totalread.addAndGet(nread));
		return totalread.get();
	}

	/**
	 * Reads all bytes from an reader and writes them to an writer the given
	 * consumer on each read amount of characters being transferred to the
	 * writer.
	 * 
	 * @param source
	 *            the input stream to read from
	 * @param target
	 *            the path to the file
	 * @param consumer
	 *            the consumer notified about each amount of written characters
	 *            to the target
	 *
	 * @throws IOException
	 *             if an I/O error occurs when reading or writing
	 */
	public static void copy(Reader source, Writer target, IntConsumer consumer)
			throws IOException {
		char[] buf = new char[BUFFER_SIZE];
		int n;
		while ((n = source.read(buf)) > 0) {
			target.write(buf, 0, n);
			consumer.accept(n);
		}
	}
}