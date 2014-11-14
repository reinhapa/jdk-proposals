package net.reini.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.IntPredicate;

/**
 * Utility methods for copying data between a {@link InputStream} and
 * {@link OutputStream} or between a {@link Reader} and a {@link Writer}.
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
     * @param source the input stream to read from
     * @param target the path to the file
     *
     * @return the number of bytes read and successfully written
     *
     * @throws IOException if an I/O error occurs when reading or writing
     */
    public static long copy(InputStream source, OutputStream target)
            throws IOException {
        LongAdder totalread = new LongAdder();
        copy(source, target, nread -> {
            totalread.add(nread);
            return true;
        });
        return totalread.sum();
    }

    /**
     * Reads all bytes from an input stream and writes them to an output stream.
     * While doing so, the given predicate is called with the amount of bytes
     * being read, to decide whenever they should be written to the output
     * stream. If the predicate returns <code>false</code> the copy operation is
     * stopped and no more data is written to the output stream.
     *
     * @param source the input stream to read from
     * @param target the path to the file
     * @param predicate the predicate tests if the copy operation should proceed
     *
     * @throws IOException if an I/O error occurs when reading or writing
     */
    public static void copy(InputStream source, OutputStream target,
            IntPredicate predicate) throws IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        int n;
        while ((n = source.read(buf)) > 0 && predicate.test(n)) {
            target.write(buf, 0, n);
        }
    }

    /**
     * Reads all bytes from an reader and writes them to an writer.
     *
     * @param source the input stream to read from
     * @param target the path to the file
     *
     * @return the number of characters read and successfully written
     *
     * @throws IOException if an I/O error occurs when reading or writing
     */
    public static long copy(Reader source, Writer target) throws IOException {
        LongAdder totalread = new LongAdder();
        copy(source, target, nread -> {
            totalread.add(nread);
            return true;
        });
        return totalread.sum();
    }

    /**
     * Reads all characters from an reader and writes them to an writer. While
     * doing so, the given predicate is called with the amount of characters
     * being read, to decide whenever they should be written to the writer. If
     * the predicate returns <code>false</code> the copy operation is stopped
     * and no more data is written to the writer.
     *
     * @param source the input stream to read from
     * @param target the path to the file
     * @param predicate the predicate tests if the copy operation should proceed
     *
     * @throws IOException if an I/O error occurs when reading or writing
     */
    public static void copy(Reader source, Writer target, IntPredicate predicate)
            throws IOException {
        char[] buf = new char[BUFFER_SIZE];
        int n;
        while ((n = source.read(buf)) > 0 && predicate.test(n)) {
            target.write(buf, 0, n);
        }
    }
}
