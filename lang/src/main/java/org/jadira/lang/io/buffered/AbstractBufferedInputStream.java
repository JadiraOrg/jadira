/*
 *  Copyright 2013 Chris Pheby
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jadira.lang.io.buffered;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Provides the basic structure for implementing BufferedInputStreams. Subclasses typically differ in terms of how they provide the buffer.
 * Some of the functionality needs to be effectively reproduced from BufferedInputStream - such methods reference the equivalents in that class.
 */
public abstract class AbstractBufferedInputStream extends FilterInputStream {

    /** The default buffer size in bytes */
    public static int DEFAULT_BUFFER_SIZE = 8192;

    private int count;
    private int pos;

    private int markpos = -1;
    private int marklimit;

    private boolean isRestartable = true;

    protected abstract void assertBufferOpen() throws IOException;

    /**
     * Create a new instance with the default buffer size
     * @param in InputStream to be wrapped
     * @see java.io.BufferedInputStream#BufferedInputStream(InputStream)
     */
    public AbstractBufferedInputStream(InputStream in) {
        this(in, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Creates a new instance with the given buffer size in bytes.
     * @param in InputStream to be wrapped
     * @param size The size of the buffer in bytes
     * @see java.io.BufferedInputStream#BufferedInputStream(InputStream, int)
     */
    public AbstractBufferedInputStream(InputStream in, int size) {
        super(in);
        if (size <= 0) {
            throw new IllegalArgumentException("Buffer size may not be less than or equal to zero");
        }
    }

    private void fill() throws IOException {

        if (markpos < 0) {

            // No mark exists, so throw away the buffer
            pos = 0;

        } else if (pos >= limit()) {

            // Buffer exhausted
            if (markpos > 0 && !isRestartable) {

                // Throw away unnecessary early bit of buffer
                int sz = pos - markpos;
                compact(markpos, sz);
                pos = sz;
                markpos = 0;

            } else if (limit() >= marklimit) {

                // Buffer became too big so invalidate the mark and drop the buffer contents
                markpos = -1;
                pos = 0;

            } else {
                
                // Grow buffer
                int newSize = pos * 2;

                if (newSize > marklimit) {
                    newSize = marklimit;
                }

                resizeBuffer(pos, newSize);
            }
        }

        count = pos;

        int n = getInputStreamIfOpen().read(toArray(), pos, limit() - pos);

        if (n > 0) {
            count = n + pos;
        }
    }

    private InputStream getInputStreamIfOpen() throws IOException {

        InputStream input = in;

        if (input == null) {
            throw new IOException("Stream closed");
        }

        return input;
    }

    /**
     * @see java.io.BufferedInputStream#read()
     */
    public int read() throws IOException {

        if (pos >= count) {

            fill();
            if (pos >= count) {
                return -1;
            }

        }

        return getInt(pos++) & 0xff;
    }

    private int read1(byte[] b, int offset, int length) throws IOException {

        int avail = count - pos;

        if (avail <= 0) {

            if (length >= limit() && markpos < 0) {
                return getInputStreamIfOpen().read(b, offset, length);
            }

            fill();

            avail = count - pos;

            if (avail <= 0) {
                return -1;
            }

        }

        int countSize = (avail < length) ? avail : length;

        if (offset == 0) {

            get(b, pos, countSize);

        } else {

            byte[] bc = new byte[countSize];
            get(bc, pos, countSize);
            System.arraycopy(bc, 0, b, offset, countSize);

        }

        pos += countSize;
        return countSize;
    }

    /**
     * @see java.io.BufferedInputStream#read(byte[], int, int)
     */
    public int read(byte b[], int off, int len) throws IOException {

        assertBufferOpen(); // Cause exception if closed

        if ((off | len | (off + len) | (b.length - (off + len))) < 0) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }

        int n = 0;

        while (true) {

            int numberRead = read1(b, off + n, len - n);

            if (numberRead <= 0) {
                return (n == 0) ? numberRead : n;
            }

            n += numberRead;

            if (n >= len) {
                return n;
            }

            // In case of no bytes available, but not closed, return
            InputStream input = in;
            if (input != null && input.available() <= 0) {
                return n;
            }
        }
    }

    /**
     * @see java.io.BufferedInputStream#skip(long)
     */
    public long skip(long n) throws IOException {

        assertBufferOpen(); // Cause exception if closed
        if (n <= 0) {
            return 0;
        }

        long available = count - pos;

        if (available <= 0) {

            if (markpos < 0) {
                return getInputStreamIfOpen().skip(n);
            }

            fill();
            available = count - pos;
            if (available <= 0) {
                return 0;
            }
        }

        long skipped = (available < n) ? available : n;
        pos += skipped;

        return skipped;
    }

    /**
     * @see java.io.BufferedInputStream#available()
     */
    public int available() throws IOException {

        int n = count - pos;
        int avail = getInputStreamIfOpen().available();

        return n > (Integer.MAX_VALUE - avail) ? Integer.MAX_VALUE : n + avail;
    }

    /**
     * @see java.io.BufferedInputStream#mark(int)
     */
    public void mark(int readlimit) {
        marklimit = readlimit;
        markpos = pos;
    }

    /**
     * @see java.io.BufferedInputStream#reset()
     */
    public void reset() throws IOException {

        assertBufferOpen(); // Cause exception if closed

        if (markpos < 0) {
            throw new IOException("Resetting to invalid mark");
        }

        pos = markpos;
    }

    /**
     * @see java.io.BufferedInputStream#markSupported()
     */
    public boolean markSupported() {
        return true;
    }

    /**
     * @see java.io.BufferedInputStream#close()
     */
    public void close() throws IOException {

        try {
            if (in != null) {
                in.close();
            }
        } finally {
            clean();
        }
    }

    /**
     * Indicates whether the stream can be restarted
     * @return true if the stream can be restarted
     */
    public boolean isRestartable() {
        return isRestartable;
    }

    /**
     * Return the stream back to its start position
     * @throws IOException Indicates a problem occurred (e.g. the stream was closed)
     */
    public synchronized void restart() throws IOException {

        if (!isRestartable()) {
            throw new IllegalStateException("Cannot restart as current buffer is not restartable");
        }
        assertBufferOpen(); // Cause exception if closed
        pos = 0;
    }

    /**
     * Compact the buffer reducing it to the given size, offset from the given mark position
     * @param markpos The mark position to use as the start of the buffer
     * @param size Required buffer size
     */
    protected abstract void compact(int markpos, int size);

    /**
     * Return the bytes in the buffer as a byte array
     * @return The bytes
     */
    protected abstract byte[] toArray();

    /**
     * Enlarge the buffer to the new size, retaining the position
     * @param position The position
     * @param newSize The new size
     * @throws IOException Indicates a problem resizing
     */
    protected abstract void resizeBuffer(int position, int newSize) throws IOException;

    /**
     * Returns the buffer's limit
     * @return The limit
     */
    protected abstract int limit();

    /**
     * Reads four bytes (an int) from the given offset
     * @param position The offset
     * @return An int
     * @throws IOException Indicates a problem reading the int
     */
    protected abstract int getInt(int position) throws IOException;

    /**
     * Retrieve the given number of bytes from the stream
     * @param b The array into which bytes will be written
     * @param pos The offset within the array of the first byte to be written
     * @param cnt The maximum number of bytes to be written to the given array
     */
    protected abstract void get(byte[] b, int pos, int cnt);

    /**
     * Performs any necessary cleaning of the buffer and releasing of resources.
     */
    protected abstract void clean();

    protected int getMarkLimit() {
        return marklimit;
    }
}
