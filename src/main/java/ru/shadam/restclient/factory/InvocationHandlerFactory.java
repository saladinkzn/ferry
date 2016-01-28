package ru.shadam.restclient.factory;

import org.apache.http.client.ResponseHandler;
import ru.shadam.restclient.annotations.Param;
import ru.shadam.restclient.annotations.Url;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author sala
 */
public class InvocationHandlerFactory {
    private final ResponseHandlerFactory responseHandlerFactory;
    private final ExecutionHelperFactory requestExecutorFactory;

    public InvocationHandlerFactory(ResponseHandlerFactory responseHandlerFactory, ExecutionHelperFactory requestExecutorFactory) {
        this.responseHandlerFactory = responseHandlerFactory;
        this.requestExecutorFactory = requestExecutorFactory;
    }

    public InvocationHandler createInvocationHandler(Class<?> clazz) {
        final Url annotation = clazz.getAnnotation(Url.class);
        final String baseUrl = annotation.value();
        //
        Map<Method, ExecutionHelper<?>> executorMap = new HashMap<>();
        final Method[] methods = clazz.getMethods();
        for(Method method: methods) {
            executorMap.put(method, processMethod(baseUrl, method));
        }
        return new MethodInvocationHandler(executorMap);
    }

    private ExecutionHelper<?> processMethod(String baseUrl, Method method) {
        final Url methodUrlAnnotation = method.getAnnotation(Url.class);
        final String methodUrl;
        if(methodUrlAnnotation != null) {
            methodUrl = methodUrlAnnotation.value();
        } else {
            methodUrl = "";
        }
        //
        final Class<?> returnType = method.getReturnType();
        final ResponseHandler<?> responseHandler = responseHandlerFactory.getResponseHandler(returnType);
        final Set<String> params = new HashSet<>();
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
        return requestExecutorFactory.getRequestExecutor("GET", baseUrl + methodUrl, params, indexToNameMap, responseHandler);
    }
}
