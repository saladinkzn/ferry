package ru.shadam.ferry.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shadam.ferry.factory.executor.MethodExecutorFactory;
import ru.shadam.ferry.implicit.ImplicitParameterProvider;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author sala
 */
public class ClientImplementationFactory {
    private static final Logger logger = LoggerFactory.getLogger(ClientImplementationFactory.class);
    //
    private final InvocationHandlerFactory invocationHandlerFactory;
    private final Map<String, ImplicitParameterProvider> implicitParameterProviders = new HashMap<>();

    public ClientImplementationFactory(MethodExecutorFactory methodExecutorFactory) {
        this.invocationHandlerFactory = new InvocationHandlerFactory(methodExecutorFactory, implicitParameterProviders);
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
}
