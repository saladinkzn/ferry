package ru.shadam.restclient.factory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;


/**
 * @author sala
 */
public class MethodInvocationHandler implements InvocationHandler {
    private final Map<Method, ExecutionHelper<?>> executorMap;

    public MethodInvocationHandler(Map<Method, ExecutionHelper<?>> methodExecutorMap) {
        this.executorMap = methodExecutorMap;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return executorMap.get(method).execute(args);
    }

}
