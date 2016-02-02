package ru.shadam.restclient.factory;

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
    private final Map<Method, MethodExecutionContext<?>> methodExecutionContextMap;

    MethodInvocationHandler(Map<Method, MethodExecutionContext<?>> methodExecutionContextMap) {
        Objects.requireNonNull(methodExecutionContextMap);
        this.methodExecutionContextMap = methodExecutionContextMap;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final MethodExecutionContext methodExecutionContext = methodExecutionContextMap.get(method);
        return execute(methodExecutionContext, args);
    }

    private <T> T execute(MethodExecutionContext<T> methodExecutionContext, Object[] args) throws IOException {
        final MethodExecutor<T> methodExecutor = methodExecutionContext.methodExecutor;
        final Map<Integer, String> indexToParamMap = methodExecutionContext.indexToParamMap;
        final Map<String, String> constImplicitParamMap = methodExecutionContext.constImplicitParamMap;
        //
        final Map<String, Object> paramToValueMap = new LinkedHashMap<>();
        for(Map.Entry<String, String> implicitParamEntry : constImplicitParamMap.entrySet()) {
            final String paramName = implicitParamEntry.getKey();
            final String value = implicitParamEntry.getValue();
            //
            paramToValueMap.put(paramName, value);
        }
        for (final Map.Entry<Integer, String> indexToParam : indexToParamMap.entrySet()) {
            final Integer index = indexToParam.getKey();
            final String paramName = indexToParam.getValue();
            final Object value = args[index];
            paramToValueMap.put(paramName, value);
        }
        return methodExecutor.execute(paramToValueMap);
    }

    static class MethodExecutionContext<T> {
        private final MethodExecutor<T> methodExecutor;
        private final Map<Integer, String> indexToParamMap;
        private final Map<String, String> constImplicitParamMap;

        public MethodExecutionContext(MethodExecutor<T> methodExecutor, Map<Integer, String> indexToParamMap, Map<String, String> constImplicitParamMap) {
            this.methodExecutor = methodExecutor;
            this.indexToParamMap = indexToParamMap;
            this.constImplicitParamMap = constImplicitParamMap;
        }
    }

}
