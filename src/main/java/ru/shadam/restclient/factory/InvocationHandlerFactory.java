package ru.shadam.restclient.factory;

import ru.shadam.restclient.analyze.InterfaceContext;
import ru.shadam.restclient.analyze.MethodContext;
import ru.shadam.restclient.analyze.impl.DefaultInterfaceContext;
import ru.shadam.restclient.analyze.impl.DefaultMethodContext;
import ru.shadam.restclient.annotations.Param;
import ru.shadam.restclient.annotations.RequestMethod;
import ru.shadam.restclient.annotations.Url;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
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
        final Method[] methods = clazz.getMethods();
        for(Method method: methods) {
            executorMap.put(method, processMethod(interfaceContext, method));
        }
        return new MethodInvocationHandler(executorMap);
    }

    private MethodExecutor<?> processMethod(InterfaceContext interfaceContext, Method method) {
        final MethodContext methodContext = getMethodContext(interfaceContext, method);
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
        final RequestMethod methodAnnotation = clazz.getAnnotation(RequestMethod.class);
        final String defaultMethod;
        if(methodAnnotation == null) {
            defaultMethod = "GET";
        } else {
            defaultMethod = methodAnnotation.value();
        }
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
        final String httpMethod;
        final RequestMethod methodAnnotation = method.getAnnotation(RequestMethod.class);
        if(methodAnnotation == null) {
            httpMethod = interfaceContext.defaultMethod();
        } else {
            httpMethod = methodAnnotation.value();
        }
        //
        final LinkedHashSet<String> params = new LinkedHashSet<>();
        final Map<Integer, String> indexToNameMap = new HashMap<>();
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
