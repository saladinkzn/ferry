package ru.shadam.restclient.factory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author sala
 */
class MethodInvocationHandler implements InvocationHandler {
    private final Map<Method, MethodExecutor<?>> executorMap;
    private final Map<Method, Map<Integer, String>> methodIndexToParamMap;
    private final Map<Method, Map<String, String>> methodImplicitParamsMap;

    MethodInvocationHandler(Map<Method, MethodExecutor<?>> methodExecutorMap, Map<Method, Map<Integer, String>> methodIndexToParamMap, Map<Method, Map<String, String>> methodImplicitParamsMap) {
        this.executorMap = methodExecutorMap;
        this.methodIndexToParamMap = methodIndexToParamMap;
        this.methodImplicitParamsMap = methodImplicitParamsMap;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return execute(executorMap.get(method), methodIndexToParamMap.get(method), methodImplicitParamsMap.get(method), args);
    }

    public <T> T execute(MethodExecutor<T> methodExecutor, Map<Integer, String> indexToNameMap, Map<String, String> constImplicitParams, Object[] args) throws IOException {
        final Map<String, Object> paramToValueMap = new LinkedHashMap<>();
        for(Map.Entry<String, String> implicitParamEntry : constImplicitParams.entrySet()) {
            final String paramName = implicitParamEntry.getKey();
            final String value = implicitParamEntry.getValue();
            //
            paramToValueMap.put(paramName, value);
        }
        for (final Map.Entry<Integer, String> indexToParam : indexToNameMap.entrySet()) {
            final Integer index = indexToParam.getKey();
            final String paramName = indexToParam.getValue();
            final Object value = args[index];
            paramToValueMap.put(paramName, value);
        }
        return methodExecutor.execute(paramToValueMap);
    }


}
