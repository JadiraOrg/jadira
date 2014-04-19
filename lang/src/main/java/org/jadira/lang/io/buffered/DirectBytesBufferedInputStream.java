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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A BufferedInputStream where the buffer is provided by a NIO DirectByteBuffer. Use this class as an alternative to {@link java.io.BufferedInputStream}
 */
public class DirectBytesBufferedInputStream extends AbstractBufferedInputStream {

    private ByteBuffer buf;

    /**
     * Create a new instance with the default buffer size
     * @param in InputStream to be wrapped
     * @see java.io.BufferedInputStream#BufferedInputStream(InputStream)
     */
    public DirectBytesBufferedInputStream(InputStream in) {
        this(in, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Creates a new instance with the given buffer size in bytes.
     * @param in InputStream to be wrapped
     * @param size The size of the buffer in bytes
     * @see java.io.BufferedInputStream#BufferedInputStream(InputStream, int)
     */
    public DirectBytesBufferedInputStream(InputStream in, int size) {
        super(in, size);
        buf = ByteBuffer.allocateDirect(size);
    }

    /**
     * Creates a new instance with the given buffer size in bytes.
     * @param in InputStream to be wrapped
     * @param size The size of the buffer in bytes
     * @param useNativeByteOrder If true, native byte ordering will be used
     * @see java.io.BufferedInputStream#BufferedInputStream(InputStream, int)
     */
    public DirectBytesBufferedInputStream(InputStream in, int size, boolean useNativeByteOrder) {
        this(in, size);
        buf.order(ByteOrder.nativeOrder());
    }

    /**
     * Creates a new instance with the given buffer size in bytes.
     * @param in InputStream to be wrapped
     * @param size The size of the buffer in bytes
     * @param byteOrder Explicitly configure the byte order to be used.
     * @see java.io.BufferedInputStream#BufferedInputStream(InputStream, int)
     */
    public DirectBytesBufferedInputStream(InputStream in, int size, ByteOrder byteOrder) {
        this(in, size);
        buf.order(byteOrder);
    }

    @Override
    protected void assertBufferOpen() throws IOException {
        if (buf == null) {
            throw new IOException("Buffer was closed");
        }
    }

    @Override
    protected void compact(int markPos, int size) {
        buf.position(markPos);
        buf.compact();
    }

    @Override
    protected byte[] toArray() {
        return buf.array();
    }

    @Override
    protected void resizeBuffer(int position, int newSize) throws IOException {

        if (newSize > getMarkLimit()) {
            newSize = getMarkLimit();
        }

        ByteBuffer nbuf = ByteBuffer.allocateDirect(newSize);

        buf.rewind();
        nbuf.put(buf);
        nbuf.position(position);

        if (buf == null) {
            throw new IOException("Stream closed");
        }

        doClean(buf);
        buf = nbuf;
    }

    @Override
    protected int limit() {
        return buf.limit();
    }

    @Override
    protected int getInt(int position) throws IOException {
        assertBufferOpen();
        return buf.getInt(position);
    }

    @Override
    protected void get(byte[] b, int pos, int cnt) {
        buf.get(b, pos, cnt);
    }

    @Override
    protected void clean() {

        if (buf == null) {
            return;
        }

        doClean(buf);

        buf = null;
    }

    @SuppressWarnings("restriction")
    private void doClean(ByteBuffer buf) {

        if (buf == null) {
            return;
        }

        sun.misc.Cleaner cleaner = ((sun.nio.ch.DirectBuffer) buf).cleaner();

        if (cleaner != null) {
            cleaner.clean();
        }
    }
}
