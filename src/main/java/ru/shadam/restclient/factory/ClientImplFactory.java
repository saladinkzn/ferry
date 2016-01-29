package ru.shadam.restclient.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

/**
 * @author sala
 */
public class ClientImplFactory implements ResponseHandlerFactory, ExecutionHelperFactory {
    private HttpClient httpClient;
    private ObjectMapper objectMapper;
    private final InvocationHandlerFactory invocationHandlerFactory;

    public ClientImplFactory(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.invocationHandlerFactory = new InvocationHandlerFactory(this, this);
    }

    public ClientImplFactory(InvocationHandlerFactory invocationHandlerFactory) {
        this.invocationHandlerFactory = invocationHandlerFactory;
    }

    public <T> T getInterfaceImplementation(Class<T> interfaceClass) {
        return ((T) Proxy.newProxyInstance(
                ClientImplFactory.class.getClassLoader(),
                new Class<?>[]{interfaceClass},
                invocationHandlerFactory.createInvocationHandler(interfaceClass)));
    }

    @Override
    public <T> ExecutionHelper<T> getRequestExecutor(String method, String url, Set<String> params, Map<Integer, String> indexToParamNameMap, ResponseHandler<T> responseHandler) {
        return new ExecutionHelper<>(httpClient, method, params, url, responseHandler, indexToParamNameMap);
    }

    @Override
    public <T> ResponseHandler<T> getResponseHandler(Type type) {
        return new ObjectMapperResponseHandler<>(objectMapper, type);
    }

    static class ObjectMapperResponseHandler<T> implements ResponseHandler<T> {
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
}
