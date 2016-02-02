package ru.shadam.restclient.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import ru.shadam.restclient.analyze.MethodContext;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @author sala
 */
public class ClientImplFactory implements ResponseHandlerFactory, MethodExecutorFactory {
    private HttpClient httpClient;
    private ObjectMapper objectMapper;
    private final ResponseHandlerFactory responseHandlerFactory;
    private final InvocationHandlerFactory invocationHandlerFactory;

    public ClientImplFactory(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.responseHandlerFactory = this;
        this.invocationHandlerFactory = new InvocationHandlerFactory(this);
    }

    public <T> T getInterfaceImplementation(Class<T> interfaceClass) {
        return ((T) Proxy.newProxyInstance(
                ClientImplFactory.class.getClassLoader(),
                new Class<?>[]{interfaceClass},
                invocationHandlerFactory.createInvocationHandler(interfaceClass)));
    }

    @Override
    public <T> MethodExecutor<T> getRequestExecutor(MethodContext methodContext) {
        final LinkedHashSet<String> params = methodContext.params();
        final String url = methodContext.url();
        final String method = methodContext.method();
        final Map<Integer, String> indexToParamNameMap = methodContext.indexToParamMap();
        //
        final ResponseHandler<T> responseHandler = responseHandlerFactory.getResponseHandler(methodContext.returnType());
        //
        return new MethodExecutor<>(httpClient, method, url, responseHandler);
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
