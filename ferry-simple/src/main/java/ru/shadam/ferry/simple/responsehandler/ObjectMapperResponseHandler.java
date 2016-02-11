package ru.shadam.ferry.simple.responsehandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.shadam.ferry.factory.response.ResponseWrapper;
import ru.shadam.ferry.factory.result.ResultExtractor;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author sala
 */
public class ObjectMapperResponseHandler<T> implements ResultExtractor<T> {
    private ObjectMapper objectMapper;
    private Type returnType;

    public ObjectMapperResponseHandler(ObjectMapper objectMapper, Type returnType) {
        this.objectMapper = objectMapper;
        this.returnType = returnType;
    }

    @Override
    public T extractResponse(ResponseWrapper responseWrapper) throws IOException{
        if(responseWrapper == null) {
            return null;
        }
        return objectMapper.readValue(responseWrapper.getInputStream(), objectMapper.getTypeFactory().constructType(returnType));
    }
}
