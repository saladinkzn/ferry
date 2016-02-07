package ru.shadam.ferry.factory.responsehandler;

import org.apache.http.client.ResponseHandler;

import java.lang.reflect.Type;

/**
 * ResponseHandler creation abstraction. Users implements this interface to provide custom response parsing
 *
 * @author sala
 * @see ObjectMapperResponseHandlerFactory
 */
public interface ResponseHandlerFactory {
    /**
     * Provides ResponseHandler for given type
     *
     * @param type Result type info
     * @param <T> Result type
     * @return ResponseHandler
     */
    <T> ResponseHandler<T> getResponseHandler(Type type);
}
