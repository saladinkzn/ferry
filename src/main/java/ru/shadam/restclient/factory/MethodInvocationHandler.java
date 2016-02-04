package ru.shadam.restclient.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shadam.restclient.factory.executor.MethodExecutor;
import ru.shadam.restclient.implicit.ImplicitParameterProvider;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;


/**
 * @author sala
 */
class MethodInvocationHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(MethodInvocationHandler.class);
    //
    private final Map<Method, MethodExecutionContext<?>> methodExecutionContextMap;
    private final Map<String, ImplicitParameterProvider> providerMap;

    public MethodInvocationHandler(Map<Method, MethodExecutionContext<?>> methodExecutionContextMap, Map<String, ImplicitParameterProvider> providerMap) {
        Objects.requireNonNull(methodExecutionContextMap);
        Objects.requireNonNull(providerMap);
        this.methodExecutionContextMap = methodExecutionContextMap;
        this.providerMap = providerMap;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final MethodExecutionContext methodExecutionContext = methodExecutionContextMap.get(method);
        if(methodExecutionContext == null) {
            // I'm not really sure if this can happen.
            logger.error("Cannot find MethodExecutionContext for method: {}", method);
            throw new IllegalStateException("MethodExecutionContext for method: " + method + " was not found");
        }
        logger.debug("Executing method: method: {}, args: {}", method, args);
        return execute(methodExecutionContext, args);
    }

    private <T> T execute(MethodExecutionContext<T> methodExecutionContext, Object[] args) throws IOException {
        //
        final MethodExecutor<T> methodExecutor = methodExecutionContext.methodExecutor;
        final Map<Integer, String> indexToParamMap = methodExecutionContext.indexToParamMap;
        final Map<String, String> constImplicitParamMap = methodExecutionContext.constImplicitParamMap;
        final Map<String, String> implicitParameterProviderMap = methodExecutionContext.implicitParameterProviderMap;
        //
        final Map<String, Object> paramToValueMap = new LinkedHashMap<>();
        for(Map.Entry<String, String> implicitParamEntry : constImplicitParamMap.entrySet()) {
            final String paramName = implicitParamEntry.getKey();
            final String value = implicitParamEntry.getValue();
            //
            if(logger.isTraceEnabled()) {
                logger.trace("Adding const implicit param: {}={}", paramName, value);
            }
            //
            paramToValueMap.put(paramName, value);
        }
        for(Map.Entry<String, String> nameToProvider : implicitParameterProviderMap.entrySet()) {
            final String paramName = nameToProvider.getKey();
            final String providerName = nameToProvider.getValue();
            // TODO: check if provider exists
            final String value = providerMap.get(providerName).provideValue();
            if(logger.isTraceEnabled()) {
                logger.trace("Adding provided implicit param: {}={}", paramName, value);
            }
            paramToValueMap.put(paramName, value);
        }
        for (final Map.Entry<Integer, String> indexToParam : indexToParamMap.entrySet()) {
            final Integer index = indexToParam.getKey();
            final String paramName = indexToParam.getValue();
            final Object value = args[index];
            if(logger.isTraceEnabled()) {
                logger.trace("Adding arg param: {}={}", paramName, value);
            }
            paramToValueMap.put(paramName, value);
        }

        return methodExecutor.execute(paramToValueMap);
    }

    static class MethodExecutionContext<T> {
        private final MethodExecutor<T> methodExecutor;
        private final Map<Integer, String> indexToParamMap;
        private final Map<String, String> constImplicitParamMap;
        private final Map<String, String> implicitParameterProviderMap;

        public MethodExecutionContext(MethodExecutor<T> methodExecutor, Map<Integer, String> indexToParamMap, Map<String, String> constImplicitParamMap, Map<String, String> implicitParameterProviderMap) {
            this.methodExecutor = methodExecutor;
            this.indexToParamMap = indexToParamMap;
            this.constImplicitParamMap = constImplicitParamMap;
            this.implicitParameterProviderMap = implicitParameterProviderMap;
        }

        @Override
        public String toString() {
            return "MethodExecutionContext{" +
                    "methodExecutor=" + methodExecutor +
                    ", indexToParamMap=" + indexToParamMap +
                    ", constImplicitParamMap=" + constImplicitParamMap +
                    ", implicitParameterProviderMap=" + implicitParameterProviderMap +
                    '}';
        }
    }

}
