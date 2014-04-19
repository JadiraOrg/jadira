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

import java.io.IOException;
import java.io.InputStream;

/**
 * A BufferedInputStream where the buffer is provided by a byte array. Use this class as an alternative to {@link java.io.BufferedInputStream}
 */
public class ByteArrayBufferedInputStream extends AbstractBufferedInputStream {

    private byte[] buf;

    /**
     * Create a new instance with the default buffer size
     * @param in InputStream to be wrapped
     * @see java.io.BufferedInputStream#BufferedInputStream(InputStream)
     */
    public ByteArrayBufferedInputStream(InputStream in) {
        this(in, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Creates a new instance with the given buffer size in bytes.
     * @param in InputStream to be wrapped
     * @param size The size of the buffer in bytes
     * @see java.io.BufferedInputStream#BufferedInputStream(InputStream, int)
     */
    public ByteArrayBufferedInputStream(InputStream in, int size) {
        super(in, size);
        buf = new byte[size];
    }

    @Override
    protected void assertBufferOpen() throws IOException {
        if (buf == null) {
            throw new IOException("Stream closed");
        }
    }

    @Override
    protected void compact(int markpos, int size) {
        System.arraycopy(buf, markpos, buf, 0, size);
    }

    @Override
    protected byte[] toArray() {
        return buf;
    }

    @Override
    protected void resizeBuffer(int position, int newSize) throws IOException {

        assertBufferOpen();

        if (newSize > getMarkLimit()) {
            newSize = getMarkLimit();
        }

        byte nbuf[] = new byte[newSize];
        System.arraycopy(buf, 0, nbuf, 0, position);

        if (buf == null) {
            throw new IOException("Stream closed");
        }

        buf = nbuf;
    }

    @Override
    protected int limit() {
        return buf.length;
    }

    @Override
    protected int getInt(int position) throws IOException {
        assertBufferOpen();
        return buf[position];
    }

    @Override
    protected void get(byte[] b, int pos, int cnt) {
        System.arraycopy(buf, pos, b, 0, cnt);
    }

    @Override
    protected void clean() {
        if (buf == null) {
            return;
        }

        buf = null;
    }
}
