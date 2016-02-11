package ru.shadam.ferry.simple.responsehandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.ResponseHandler;
import ru.shadam.ferry.analyze.MethodContext;
import ru.shadam.ferry.factory.result.ResultExtractor;
import ru.shadam.ferry.factory.result.ResultExtractorFactory;

import java.lang.reflect.Type;

/**
 * Jackson-based {@link ResponseHandlerFactory} implementation
 *
 * @author sala
 * @see ResponseHandlerFactory
 * @see ObjectMapper
 */
public class ObjectMapperResponseHandlerFactory implements ResultExtractorFactory {
    private final ObjectMapper objectMapper;

    public ObjectMapperResponseHandlerFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> ResultExtractor<T> getResultExtractor(MethodContext methodContext) {
        return new ObjectMapperResponseHandler<T>(objectMapper, methodContext.returnType());
    }
}
