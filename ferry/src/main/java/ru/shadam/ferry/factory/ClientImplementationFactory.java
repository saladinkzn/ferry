package ru.shadam.ferry.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shadam.ferry.factory.converter.RequestBodyConverter;
import ru.shadam.ferry.factory.executor.MethodExecutorFactory;
import ru.shadam.ferry.factory.result.ResultExtractorFactory;
import ru.shadam.ferry.factory.result.UnsupportedTypeException;
import ru.shadam.ferry.implicit.ImplicitParameterProvider;
import ru.shadam.ferry.util.MoreObjects;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation proxy factory.
 *
 * Usage sample:
 * <pre><code>
 *     ClientImplementationFactory cif = new DefaultClientImplementationFactory(httpClient, objectMapper);
 *     PhotoRepository photoRepository = cif.getInterfaceImplementation(PhotoRepository.class);
 * </code></pre>
 *
 * @author sala
 */
public class ClientImplementationFactory {
    private static final Logger logger = LoggerFactory.getLogger(ClientImplementationFactory.class);
    //
    private final InvocationHandlerFactory invocationHandlerFactory;
    private final Map<String, ImplicitParameterProvider> implicitParameterProviders = new HashMap<String, ImplicitParameterProvider>();

    public ClientImplementationFactory(MethodExecutorFactory methodExecutorFactory, ResultExtractorFactory resultExtractorFactory, RequestBodyConverter requestBodyConverter) {
        this.invocationHandlerFactory = new InvocationHandlerFactory(methodExecutorFactory, resultExtractorFactory, implicitParameterProviders, requestBodyConverter);
    }

    public <T> T getInterfaceImplementation(Class<T> interfaceClass) throws UnsupportedTypeException {
        logger.debug("Getting interface implementation for {}", interfaceClass);
        return ((T) Proxy.newProxyInstance(
                ClientImplementationFactory.class.getClassLoader(),
                new Class<?>[]{interfaceClass},
                invocationHandlerFactory.createInvocationHandler(interfaceClass)));
    }

    public void registerImplicitParameterProvider(String name, ImplicitParameterProvider provider) {
        logger.debug("Registering implicit parameter provider name: {}, provider: {}", name, provider);
        MoreObjects.requireNonNull(name);
        MoreObjects.requireNonNull(provider);
        implicitParameterProviders.put(name, provider);
    }
}
