package ru.shadam.ferry.factory.result;

import ru.shadam.ferry.factory.response.ResponseWrapper;

import java.io.IOException;

/**
 * Abstraction for extracting value from response wrapper
 *
 * @author sala
 */
public interface ResultExtractor<T> {
    T extractResponse(ResponseWrapper responseWrapper) throws IOException;
}
