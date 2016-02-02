package ru.shadam.restclient.factory;

import ru.shadam.restclient.analyze.InterfaceContext;
import ru.shadam.restclient.analyze.MethodContext;
import ru.shadam.restclient.analyze.impl.DefaultInterfaceContext;
import ru.shadam.restclient.analyze.impl.DefaultMethodContext;
import ru.shadam.restclient.annotations.Param;
import ru.shadam.restclient.annotations.Url;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @author sala
 */
public class InvocationHandlerFactory {
    private final MethodExecutorFactory methodExecutorFactory;

    public InvocationHandlerFactory(MethodExecutorFactory methodExecutorFactory) {
        this.methodExecutorFactory = methodExecutorFactory;
    }

    public InvocationHandler createInvocationHandler(Class<?> clazz) {
        final InterfaceContext interfaceContext = getInterfaceContext(clazz);
        //
        Map<Method, MethodExecutor<?>> executorMap = new HashMap<>();
        Map<Method, Map<Integer, String>> methodIndexToParamMap = new HashMap<>();
        final Method[] methods = clazz.getMethods();
        for(Method method: methods) {
            final MethodContext methodContext = getMethodContext(interfaceContext, method);
            executorMap.put(method, processMethod(methodContext));
            methodIndexToParamMap.put(method, getIndexToParamMap(methodContext));
        }
        return new MethodInvocationHandler(executorMap, methodIndexToParamMap);
    }

    private Map<Integer, String> getIndexToParamMap(MethodContext methodContext) {
        return methodContext.indexToParamMap();
    }

    private MethodExecutor<?> processMethod(MethodContext methodContext) {
        return methodExecutorFactory.getRequestExecutor(methodContext);
    }

    static InterfaceContext getInterfaceContext(Class<?> clazz) {
        final Url urlAnnotation = clazz.getAnnotation(Url.class);
        final String baseUrl;
        if(urlAnnotation != null) {
            baseUrl = urlAnnotation.value();
        } else {
            baseUrl = "";
        }
        //
        // TODO:
        final String defaultMethod = "GET";
        //
        return new DefaultInterfaceContext(baseUrl, defaultMethod);
    }

    static MethodContext getMethodContext(InterfaceContext interfaceContext, Method method) {
        final Type returnType = method.getGenericReturnType();
        final String url;
        final Url urlAnnotation = method.getAnnotation(Url.class);
        if(urlAnnotation == null) {
            url = null;
        } else {
            url = urlAnnotation.value();
        }
        // TODO:
        //
        final String httpMethod = "GET";
        //
        final LinkedHashSet<String> params = new LinkedHashSet<>();
        final Map<Integer, String> indexToNameMap = new LinkedHashMap<>();
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for(int paramIndex = 0; paramIndex < parameterAnnotations.length; paramIndex++) {
            final Annotation[] parameterAnnotationArray = parameterAnnotations[paramIndex];
            for(Annotation annotation : parameterAnnotationArray) {
                if(annotation.annotationType().isAssignableFrom(Param.class)) {
                    final Param param = ((Param) annotation);
                    final String paramName = param.value();
                    params.add(paramName);
                    indexToNameMap.put(paramIndex, paramName);
                    break;
                }
            }
        }
        return new DefaultMethodContext(interfaceContext, url, httpMethod, params, indexToNameMap, returnType);
    }
}
