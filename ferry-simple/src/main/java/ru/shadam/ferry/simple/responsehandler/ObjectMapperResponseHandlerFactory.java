package ru.shadam.ferry.simple.responsehandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.shadam.ferry.analyze.MethodContext;
import ru.shadam.ferry.factory.result.ResultExtractor;
import ru.shadam.ferry.factory.result.ResultExtractorFactory;
import ru.shadam.ferry.factory.result.UnsupportedTypeException;

/**
 * Jackson-based {@link ResultExtractorFactory} implementation
 *
 * @author sala
 * @see ResultExtractorFactory
 * @see ObjectMapper
 */
public class ObjectMapperResponseHandlerFactory implements ResultExtractorFactory {
    private final ObjectMapper objectMapper;

    public ObjectMapperResponseHandlerFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean canCreateExtractor(MethodContext methodContext) {
        if(Void.class.equals(methodContext.returnType())) {
            return true;
        }
        return objectMapper.canDeserialize(objectMapper.constructType(methodContext.returnType()));
    }

    @Override
    public <T> ResultExtractor<T> getResultExtractor(MethodContext methodContext) {
        if(!canCreateExtractor(methodContext)) {
            throw new UnsupportedTypeException(methodContext.returnType());
        }
        return new ObjectMapperResponseHandler<T>(objectMapper, methodContext.returnType());
    }
}
