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
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

/**
 * An OutputStream that writes to a ByteBuffer. This can be used to adapt the NIO type to standard Java IO.
 */
public class ByteBufferBackedOutputStream extends OutputStream {

    private boolean isClosed = false;

    ByteBuffer buf;

    public ByteBufferBackedOutputStream(ByteBuffer buf) {
        this.buf = buf;
    }

    @Override
    public void write(int b) throws IOException {

        if (isClosed) {
            throw new IOException("ByteBufferOutputStream was already closed");
        }
        try {
            buf.put((byte) b);
        } catch (BufferOverflowException e) {
            throw new IOException("Buffer is full: " + e.getMessage(), e);
        }
    }

    @Override
    public void write(byte[] bytes, int off, int len) throws IOException {

        if (isClosed) {
            throw new IOException("ByteBufferOutputStream was already closed");
        }
        try {
            buf.put(bytes, off, len);
        } catch (BufferOverflowException e) {
            throw new IOException("Buffer is full: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        isClosed = true;
    }
}
