package ru.shadam.restclient.factory;

import org.apache.http.client.ResponseHandler;

import java.lang.reflect.Type;

/**
 * @author sala
 */
public interface ResponseHandlerFactory {
    <T> ResponseHandler<T> getResponseHandler(Type type);
}
