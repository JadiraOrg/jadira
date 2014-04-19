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

import java.io.OutputStream;

/**
 * A BufferedOutputStream where the buffer is provided by a byte array. Use this class as an alternative to {@link java.io.BufferedOutputStream}
 */
public class ByteArrayBufferedOutputStream extends AbstractBufferedOutputStream {

    private byte[] buf;

    /**
     * Creates a new instance with the default buffer size
     * @param out OutputStream to be decorated
     * @see java.io.BufferedOutputStream#BufferedOutputStream(OutputStream)
     */
    public ByteArrayBufferedOutputStream(OutputStream out) {
        this(out, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Creates a new instance with the given buffer size in bytes.
     * @param out OutputStream to be decorated
     * @param size The size of the buffer in bytes
     * @see java.io.BufferedOutputStream#BufferedOutputStream(OutputStream, int)
     */
    public ByteArrayBufferedOutputStream(OutputStream out, int size) {
        super(out);
        if (size <= 0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        buf = new byte[size];
    }

    @Override
    protected byte[] bytes() {
        return buf;
    }

    @Override
    protected void put(byte b) {
        put(b);
    }

    @Override
    protected void put(int count, byte[] b, int off, int len) {
        System.arraycopy(b, off, buf, count, len);
    }

    @Override
    protected int limit() {
        return buf.length;
    }

    @Override
    protected void clean() {
        if (buf == null) {
            return;
        }

        buf = null;
    }
}