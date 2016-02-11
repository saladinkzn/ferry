package ru.shadam.ferry.factory.response;

import java.io.InputStream;

/**
 * Simple ResponseWrapper implementation
 *
 * @author sala
 */
public class DefaultResponseWrapper implements ResponseWrapper {
    private final InputStream inputStream;

    public DefaultResponseWrapper(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }
}
