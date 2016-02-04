package ru.shadam.restclient.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shadam.restclient.analyze.MethodContext;
import ru.shadam.restclient.implicit.ImplicitParameterProvider;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author sala
 */
public class ClientImplementationFactory implements ResponseHandlerFactory, MethodExecutorFactory {
    private static final Logger logger = LoggerFactory.getLogger(ClientImplementationFactory.class);
    //
    private HttpClient httpClient;
    private ObjectMapper objectMapper;
    private final ResponseHandlerFactory responseHandlerFactory;
    private final InvocationHandlerFactory invocationHandlerFactory;
    private final Map<String, ImplicitParameterProvider> implicitParameterProviders = new HashMap<>();

    public ClientImplementationFactory(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.responseHandlerFactory = this;
        this.invocationHandlerFactory = new InvocationHandlerFactory(this, implicitParameterProviders);
    }

    public <T> T getInterfaceImplementation(Class<T> interfaceClass) {
        logger.debug("Getting interface implementation for {}", interfaceClass);
        return ((T) Proxy.newProxyInstance(
                ClientImplementationFactory.class.getClassLoader(),
                new Class<?>[]{interfaceClass},
                invocationHandlerFactory.createInvocationHandler(interfaceClass)));
    }

    public void registerImplicitParameterProvider(String name, ImplicitParameterProvider provider) {
        logger.debug("Registering implicit parameter provider name: {}, provider: {}", name, provider);
        Objects.requireNonNull(name);
        Objects.requireNonNull(provider);
        implicitParameterProviders.put(name, provider);
    }

    @Override
    public <T> MethodExecutor<T> getRequestExecutor(MethodContext methodContext) {
        Objects.requireNonNull(methodContext);
        //
        logger.debug("Getting request executor for method context: {}", methodContext);
        //
        final String url = methodContext.url();
        final String method = methodContext.method();
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
