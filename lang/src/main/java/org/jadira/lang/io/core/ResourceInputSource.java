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
package org.jadira.lang.io.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import javax.xml.transform.sax.SAXSource;

import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.xml.sax.InputSource;

/**
 * An InputSource suitable constructed from an accessible resource. It can be constructed from a stream, URL, file or Spring Framework resource.
 */
public class ResourceInputSource extends InputSource {

    private String fileName;

    private File file;

    private InputStreamSource streamSource;

    private Resource resource;

    /**
     * Creates a ResourceInputSource for the given {@link URL}
     * @param url The URL whose contents will be parsed
     * @throws IOException Indicates a problem accessing the URL
     */
    public ResourceInputSource(URL url) throws IOException {

        if (url.getProtocol().equals("file")) {
            doSetFile(new File(url.getFile()));
        }

        super.setByteStream(url.openStream());

        String urlName = url.getFile();

        if (urlName.lastIndexOf('/') != -1) {
            this.fileName = urlName.substring(urlName.lastIndexOf('/') + 1);
        } else {
            this.fileName = urlName;
        }
    }

    /**
     * Creates a ResourceInputSource for the given {@link InputStreamSource}
     * @param streamSource The InputStreamSource to be parsed
     * @throws IOException Indicates a problem in accessing the underlying stream
     */
    public ResourceInputSource(InputStreamSource streamSource) throws IOException {
        setByteStream(streamSource.getInputStream());

        this.streamSource = streamSource;
    }

    /**
     * Creates a ResourceInputSource for the given {@link Resource}
     * @param resource The Resource to be parsed
     * @throws IOException Indicates a problem in accessing the actual resource
     */
    public ResourceInputSource(Resource resource) throws IOException {

        try {
            doSetFile(resource.getFile());
        } catch (IOException e) {
            // Ignore - the resource will be accessed instead via its stream
        }
        setByteStream(resource.getInputStream());

        this.resource = resource;
    }

    /**
     * Creates a ResourceInputSource for the given {@link InputStream}
     * @param byteStream The byte stream to be parsed
     */
    public ResourceInputSource(InputStream byteStream) {
        setByteStream(byteStream);
    }

    /**
     * Creates a ResourceInputSource for the given {@link InputStreamSource} and name
     * @param byteStream The byte stream to be parsed
     * @param fileName The file name for the file
     */
    public ResourceInputSource(InputStream byteStream, String fileName) {
        super.setByteStream(byteStream);
        this.fileName = fileName;
    }

    /**
     * Creates a ResourceInputSource for the given {@link File}
     * @param file The file to be parsed
     */
    public ResourceInputSource(File file) {
        doSetFile(file);
    }

    private void doSetFile(File file) {
        this.file = file;
    }

    public void setByteStream(InputStream byteStream) {
        super.setByteStream(byteStream);
    }

    /**
     * Returns the file name for this {@link InputSource} instance, if any
     * @return The file's name
     */
    public String getFileName() {
        if (file != null) {
            return file.getName();
        } else {
            return fileName;
        }
    }

    /**
     * Return the associated file, if any
     * @return The File
     */
    public File getFile() {
        return file;
    }

    /**
     * Return the associated {@link InputStreamSource}, if any
     * @return The InputStreamSource
     */
    public InputStreamSource getStreamSource() {
        return streamSource;
    }

    /**
     * Return the associated Resource, if any
     * @return The Resource
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * Returns this instance wrapped as a {@link SAXSource}
     * @return The new {@link SAXSource}
     */
    public SAXSource asSAXSource() {
        return new SAXSource(this);
    }

    /**
     * This method from {@link InputSource} is Unsupported
     */
    @Override
    public void setCharacterStream(Reader reader) {
        throw new UnsupportedOperationException("CharacterStream is not supported");
    }

    /**
     * This method from {@link InputSource} is Unsupported
     */
    @Override
    public void setPublicId(String publicId) {
        throw new UnsupportedOperationException("PublicId is not supported");
    }

    /**
     * This method from {@link InputSource} is Unsupported
     */
    @Override
    public void setSystemId(String systemId) {
        throw new UnsupportedOperationException("SystemId is not supported");
    }
}
