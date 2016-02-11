package ru.shadam.ferry.factory.response;

import java.io.InputStream;

/**
 * Abstraction for http request response.
 *
 * @author sala
 */
public interface ResponseWrapper {
    /**
     * @return Response body as input stream
     */
    InputStream getInputStream();
}
