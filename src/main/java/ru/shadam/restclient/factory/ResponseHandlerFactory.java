package ru.shadam.restclient.factory;

import org.apache.http.client.ResponseHandler;

/**
 * @author sala
 */
public interface ResponseHandlerFactory {
    public <T> ResponseHandler<T> getResponseHandler(Class<T> clazz);
}
