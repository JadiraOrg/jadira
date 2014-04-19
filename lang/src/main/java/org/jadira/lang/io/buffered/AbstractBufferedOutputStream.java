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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Provides the basic structure for implementing BufferedOutputStreams. Subclasses typically differ in terms of how they provide the buffer.
 * Some of the functionality needs to be effectively reproduced from BufferedOutputStream - such methods reference the equivalents in that class.
 */
public abstract class AbstractBufferedOutputStream extends FilterOutputStream {

    /** The default buffer size in bytes */
    public static int DEFAULT_BUFFER_SIZE = 8192;

    private int count;

    /**
     * A BufferedOutputStream where the buffer is provided by a byte array. Use this class as an alternative to {@link java.io.BufferedOutputStream}
     * @param out OutputStream to be decorated
     */
    public AbstractBufferedOutputStream(OutputStream out) {
        this(out, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Creates a new instance with the given buffer size in bytes.
     * @param out OutputStream to be decorated
     * @param size The size of the buffer in bytes
     * @see java.io.BufferedOutputStream#BufferedOutputStream(OutputStream, int)
     */
    public AbstractBufferedOutputStream(OutputStream out, int size) {
        super(out);

        if (size <= 0) {
            throw new IllegalArgumentException("Buffer size may not be less than or equal to zero");
        }
    }

    private void flushBuffer() throws IOException {

        if (count > 0) {
            out.write(bytes(), 0, count);
            count = 0;
        }
    }

    /**
     * @see java.io.BufferedOutputStream#write(int)
     */
    public void write(int b) throws IOException {

        if (count >= limit()) {
            flushBuffer();
        }

        put((byte) b);
    }

    /**
     * @see java.io.BufferedOutputStream#write(byte[], int, int)
     */
    public void write(byte b[], int off, int len) throws IOException {

        if (len >= limit()) {
            flushBuffer();
            out.write(b, off, len);
            return;
        }

        if (len > limit() - count) {
            flushBuffer();
        }

        put(count, b, off, len);
        count += len;
    }

    /**
     * @see java.io.BufferedOutputStream#flush
     */
    public void flush() throws IOException {

        flushBuffer();
        out.flush();
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            clean();
        }
    }

    /**
     * Return the bytes in the buffer as a byte array
     * @return The bytes
     */
    protected abstract byte[] bytes();

    /**
     * Put a single byte into the buffer
     * @param b The byte
     */
    protected abstract void put(byte b);

    /**
     * Puts bytes into the buffer
     * @param count The number of bytes already written into the buffer
     * @param b The byte array with the bytes to put
     * @param off Offset to write from in the bytes, b
     * @param len The number of bytes to write
     */
    protected abstract void put(int count, byte[] b, int off, int len);

    /**
     * Returns the limit of the buffer
     * @return The limit
     */
    protected abstract int limit();

    /**
     * Performs any necessary cleaning of the buffer and releasing of resources.
     */
    protected abstract void clean();
}