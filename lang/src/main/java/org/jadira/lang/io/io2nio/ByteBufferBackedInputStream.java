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
package org.jadira.lang.io.io2nio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * An InputStream that reads from a ByteBuffer. This can be used to adapt the NIO type to standard Java IO.
 */
public class ByteBufferBackedInputStream extends InputStream {

    private boolean isClosed = false;

    private ByteBuffer buf;

    private int mark = -1;

    private int readlimit = -1;

    /**
     * Creates a new instance for the given ByteBuffer.
     * @param buf The ByteBuffer to use
     */
    public ByteBufferBackedInputStream(ByteBuffer buf) {
        this.buf = buf;
    }

    @Override
    public int read() throws IOException {

        if (isClosed) {
            throw new IOException("ByteBufferInputStream was already closed");
        }

        if (!buf.hasRemaining()) {
            return -1;
        }
        try {
            final int res = buf.get() & 0xFF;
            invalidateMark();

            return res;
        } catch (BufferUnderflowException e) {
            throw new IOException("Unexpected Error reading from ByteBuffer: " + e.getMessage(), e);
        }
    }

    private void invalidateMark() {
        if (mark >= 0 && readlimit >= 0) {
            if (buf.position() > (mark + readlimit)) {
                mark = -1;
                readlimit = -1;
            }
        }
    }

    @Override
    public int read(byte[] bytes, int off, int len) throws IOException {

        if (isClosed) {
            throw new IOException("ByteBufferInputStream was already closed");
        }

        if (!buf.hasRemaining()) {
            return -1;
        }

        len = Math.min(len, buf.remaining());

        try {
            buf.get(bytes, off, len);
        } catch (BufferUnderflowException e) {
            throw new IOException("Unexpected Error reading from ByteBuffer: " + e.getMessage(), e);
        } catch (IndexOutOfBoundsException e) {
            throw new IOException("Unexpected Error reading from ByteBuffer: " + e.getMessage(), e);
        }
        invalidateMark();
        return len;
    }

    @Override
    public long skip(long n) throws IOException {

        if (isClosed) {
            throw new IOException("ByteBufferInputStream was already closed");
        }

        long bytesSkipped = 0;

        int position = buf.position();
        int remaining = buf.limit() - position;

        int len = (int) (Math.min(n, (long) remaining));

        buf.position(position + len);

        invalidateMark();
        return bytesSkipped;
    }

    public int available() throws IOException {

        if (isClosed) {
            throw new IOException("available() but ByteBufferInputStream was already closed");
        }

        return buf.limit() - buf.position();
    };

    public void close() throws IOException {
        isClosed = true;
    }

    public void mark(int readlimit) {

        mark = buf.position();
        this.readlimit = readlimit;
    }

    public void rewind() throws IOException {
        position(0);
    }

    public void position(int position) throws IOException {

        if (isClosed) {
            throw new IOException("ByteBufferInputStream was already closed");
        }

        buf.rewind();
        skip(position);
        invalidateMark();
    }

    public void reset() throws IOException {

        if (isClosed) {
            throw new IOException("ByteBufferInputStream was already closed");
        }

        if (mark < 0) {
            throw new IOException("Attempted to call reset() but mark was not valid");
        }
        buf.position(mark);
    }

    public boolean markSupported() {
        return true;
    }
}
