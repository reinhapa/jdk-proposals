package net.reini.io;

import static net.reini.io.IOUtil.copy;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

public class IOUtilTest {

    @Test
    public void copyIoStreamsUsingIntConsumer() throws IOException {
        AtomicLong counter = new AtomicLong();
        ByteArrayInputStream source = new ByteArrayInputStream("abc".getBytes());
        ByteArrayOutputStream target = new ByteArrayOutputStream();

        copy(source, target, c -> counter.addAndGet(c));

        assertEquals(3, counter.longValue());
        assertEquals("abc", target.toString());
    }

    @Test
    public void copyIoStreamsGetttingReadBytes() throws IOException {
        ByteArrayInputStream source = new ByteArrayInputStream("abc".getBytes());
        ByteArrayOutputStream target = new ByteArrayOutputStream();

        long readBytes = copy(source, target);

        assertEquals(3, readBytes);
        assertEquals("abc", target.toString());
    }

    @Test
    public void copyIoReadersUsingIntConsumer() throws IOException {
        AtomicLong counter = new AtomicLong();
        StringReader source = new StringReader("abc");
        StringWriter target = new StringWriter();

        copy(source, target, c -> counter.addAndGet(c));

        assertEquals(3, counter.longValue());
        assertEquals("abc", target.toString());
    }

    @Test
    public void copyIoReadersGetttingReadBytes() throws IOException {
        StringReader source = new StringReader("abc");
        StringWriter target = new StringWriter();

        long readChars = copy(source, target);

        assertEquals(3, readChars);
        assertEquals("abc", target.toString());
    }
}
