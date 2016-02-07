package ru.shadam.ferry.factory.responsehandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.ResponseHandler;

import java.lang.reflect.Type;

/**
 * Jackson-based {@link ResponseHandlerFactory} implementation
 *
 * @author sala
 * @see ResponseHandlerFactory
 * @see ObjectMapper
 */
public class ObjectMapperResponseHandlerFactory implements ResponseHandlerFactory {
    private final ObjectMapper objectMapper;

    public ObjectMapperResponseHandlerFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> ResponseHandler<T> getResponseHandler(Type type) {
        return new ObjectMapperResponseHandler<>(objectMapper, type);
    }
}
