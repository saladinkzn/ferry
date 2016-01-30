package ru.shadam.restclient.factory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;


/**
 * @author sala
 */
class MethodInvocationHandler implements InvocationHandler {
    private final Map<Method, MethodExecutor<?>> executorMap;

    MethodInvocationHandler(Map<Method, MethodExecutor<?>> methodExecutorMap) {
        this.executorMap = methodExecutorMap;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return executorMap.get(method).execute(args);
    }

}
