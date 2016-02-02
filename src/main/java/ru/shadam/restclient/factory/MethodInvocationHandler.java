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

    MethodInvocationHandler(Map<Method, MethodExecutor<?>> methodExecutorMap, Map<Method, Map<Integer, String>> methodIndexToParamMap) {
        this.executorMap = methodExecutorMap;
        this.methodIndexToParamMap = methodIndexToParamMap;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return execute(executorMap.get(method), methodIndexToParamMap.get(method), args);
    }

    public <T> T execute(MethodExecutor<T> methodExecutor, Map<Integer, String> indexToNameMap, Object[] args) throws IOException {
        final Map<String, Object> paramToValueMap = new LinkedHashMap<>();
        for (final Map.Entry<Integer, String> indexToParam : indexToNameMap.entrySet()) {
            final Integer index = indexToParam.getKey();
            final String paramName = indexToParam.getValue();
            paramToValueMap.put(paramName, args[index]);
        }
        return methodExecutor.execute(paramToValueMap);
    }
}
