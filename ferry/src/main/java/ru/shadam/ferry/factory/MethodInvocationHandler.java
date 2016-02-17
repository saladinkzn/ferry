package ru.shadam.ferry.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shadam.ferry.factory.converter.RequestBodyConverter;
import ru.shadam.ferry.factory.executor.MethodExecutor;
import ru.shadam.ferry.factory.response.ResponseWrapper;
import ru.shadam.ferry.factory.result.ResultExtractor;
import ru.shadam.ferry.implicit.ImplicitParameterProvider;
import ru.shadam.ferry.implicit.ImplicitParameterWithNameProvider;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
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
    private final Map<String, ? extends ImplicitParameterProvider> providerMap;
    private final RequestBodyConverter requestBodyConverter;

    public MethodInvocationHandler(Map<Method, MethodExecutionContext<?>> methodExecutionContextMap, Map<String, ? extends ImplicitParameterProvider> providerMap) {
        Objects.requireNonNull(methodExecutionContextMap);
        Objects.requireNonNull(providerMap);
        this.methodExecutionContextMap = methodExecutionContextMap;
        this.providerMap = providerMap;
        this.requestBodyConverter = new RequestBodyConverter() {
            @Override
            public <T> String convert(T value) {
                return String.valueOf(value);
            }

            @Override
            public <T> boolean canConvert(Class<T> clazz) {
                return true;
            }
        };
    }

    public MethodInvocationHandler(Map<Method, MethodExecutionContext<?>> methodExecutionContextMap, Map<String, ? extends ImplicitParameterProvider> providerMap, RequestBodyConverter requestBodyConverter) {
        Objects.requireNonNull(methodExecutionContextMap);
        Objects.requireNonNull(providerMap);
        Objects.requireNonNull(requestBodyConverter);
        this.methodExecutionContextMap = methodExecutionContextMap;
        this.providerMap = providerMap;
        this.requestBodyConverter = requestBodyConverter;
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
        final Map<Integer, String> indexToParamMap = methodExecutionContext.indexToParamMap;
        final Map<String, String> constImplicitParamMap = methodExecutionContext.constImplicitParamMap;
        final Map<String, String> implicitParameterProviderMap = methodExecutionContext.implicitParameterProviderMap;
        final Map<Integer, String> indexToPathVariableMap = methodExecutionContext.indexToPathVariableMap;
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
            final String providerName = nameToProvider.getValue();
            final ImplicitParameterProvider implicitParameterProvider = providerMap.get(providerName);
            if(implicitParameterProvider == null) {
                logger.debug("Provider {} required by {} was not found" + methodExecutionContext);
                continue;
            }
            final String value = implicitParameterProvider.provideValue();
            final String paramName;
            if(implicitParameterProvider instanceof ImplicitParameterWithNameProvider) {
                final ImplicitParameterWithNameProvider implicitParameterWithNameProvider = (ImplicitParameterWithNameProvider) implicitParameterProvider;
                final String contextParamName = nameToProvider.getKey();
                if("".equals(contextParamName)) {
                    paramName = implicitParameterWithNameProvider.parameterName();
                } else {
                    paramName = contextParamName;
                }
            } else {
                paramName = nameToProvider.getKey();
            }
            // TODO: check if provider exists
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
        final Map<String, Object> pathToVariableMap = new HashMap<>();
        for (Map.Entry<Integer, String> indexToPathVariable: indexToPathVariableMap.entrySet()) {
            final Integer index = indexToPathVariable.getKey();
            final String pathVariable = indexToPathVariable.getValue();
            final Object value = args[index];
            if(logger.isTraceEnabled()) {
                logger.trace("Adding path variable: {}={}", pathVariable, value);
            }
            pathToVariableMap.put(pathVariable, value);
        }
        final String requestBody;
        if(methodExecutionContext.requestBodyParamIndex != null) {
            final Object value = args[methodExecutionContext.requestBodyParamIndex];
            requestBody = requestBodyConverter.convert(value);
            if(logger.isTraceEnabled()) {
                logger.trace("Adding request body: {}", requestBody);
            }
        } else {
            requestBody = null;
        }
        if(methodExecutionContext.mapParamIndex != null) {
            final Map<Object, Object> value = (Map) args[methodExecutionContext.mapParamIndex];
            for(Map.Entry<Object, Object> entry : value.entrySet()) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Adding param from @Param Map: {} = {}", entry.getKey(), entry.getValue());
                }
                paramToValueMap.put(String.valueOf(entry.getKey()), entry.getValue());
            }
        }
        final MethodExecutor methodExecutor = methodExecutionContext.methodExecutor;
        final ResultExtractor<T> resultExtractor = methodExecutionContext.resultExtractor;
        final ResponseWrapper responseWrapper = methodExecutor.execute(paramToValueMap, pathToVariableMap, requestBody);
        final T result = resultExtractor.extractResponse(responseWrapper);
        return result;
    }

    static class MethodExecutionContext<T> {
        private final MethodExecutor methodExecutor;
        private final ResultExtractor<T> resultExtractor;
        private final Map<Integer, String> indexToParamMap;
        private final Map<String, String> constImplicitParamMap;
        private final Map<String, String> implicitParameterProviderMap;
        private final Map<Integer, String> indexToPathVariableMap;
        private final Integer requestBodyParamIndex;
        private final Integer mapParamIndex;

        @Deprecated
        public MethodExecutionContext(MethodExecutor methodExecutor, ResultExtractor<T> resultExtractor, Map<Integer, String> indexToParamMap, Map<String, String> constImplicitParamMap, Map<String, String> implicitParameterProviderMap, Map<Integer, String> indexToPathVariableMap) {
            this(methodExecutor, resultExtractor, indexToParamMap, constImplicitParamMap, implicitParameterProviderMap, indexToPathVariableMap, null, null);
        }

        @Deprecated
        public MethodExecutionContext(MethodExecutor methodExecutor, ResultExtractor<T> resultExtractor,
                                      Map<Integer, String> indexToParamMap,
                                      Map<String, String> constImplicitParamMap,
                                      Map<String, String> implicitParameterProviderMap,
                                      Map<Integer, String> indexToPathVariableMap,
                                      Integer requestBodyParamIndex) {
            this(methodExecutor, resultExtractor, indexToParamMap, constImplicitParamMap, implicitParameterProviderMap, indexToPathVariableMap, requestBodyParamIndex, null);
        }

        public MethodExecutionContext(MethodExecutor methodExecutor, ResultExtractor<T> resultExtractor,
                                      Map<Integer, String> indexToParamMap,
                                      Map<String, String> constImplicitParamMap,
                                      Map<String, String> implicitParameterProviderMap,
                                      Map<Integer, String> indexToPathVariableMap,
                                      Integer requestBodyParamIndex,
                                      Integer mapParamIndex) {
            this.methodExecutor = methodExecutor;
            this.resultExtractor = resultExtractor;
            this.indexToParamMap = indexToParamMap;
            this.constImplicitParamMap = constImplicitParamMap;
            this.implicitParameterProviderMap = implicitParameterProviderMap;
            this.indexToPathVariableMap = indexToPathVariableMap;
            this.requestBodyParamIndex = requestBodyParamIndex;
            this.mapParamIndex = mapParamIndex;
        }


        @Override
        public String toString() {
            return "MethodExecutionContext{" +
                    "methodExecutor=" + methodExecutor +
                    ", indexToParamMap=" + indexToParamMap +
                    ", constImplicitParamMap=" + constImplicitParamMap +
                    ", implicitParameterProviderMap=" + implicitParameterProviderMap +
                    ", indexToPathVariableMap=" + indexToPathVariableMap +
                    '}';
        }
    }

}
