package ru.shadam.ferry.factory.responsehandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author sala
 */
public class ObjectMapperResponseHandler<T> implements ResponseHandler<T> {
    private ObjectMapper objectMapper;
    private Type returnType;

    public ObjectMapperResponseHandler(ObjectMapper objectMapper, Type returnType) {
        this.objectMapper = objectMapper;
        this.returnType = returnType;
    }

    @Override
    public T handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        return objectMapper.readValue(response.getEntity().getContent(), objectMapper.getTypeFactory().constructType(returnType));
    }
}
