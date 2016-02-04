package ru.shadam.ferry.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shadam.ferry.analyze.InterfaceContext;
import ru.shadam.ferry.analyze.MethodContext;
import ru.shadam.ferry.analyze.impl.DefaultInterfaceContext;
import ru.shadam.ferry.analyze.impl.DefaultMethodContext;
import ru.shadam.ferry.annotations.*;
import ru.shadam.ferry.factory.executor.MethodExecutor;
import ru.shadam.ferry.factory.executor.MethodExecutorFactory;
import ru.shadam.ferry.implicit.ImplicitParameterProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author sala
 */
public class InvocationHandlerFactory {
    private static final Logger logger = LoggerFactory.getLogger(InvocationHandlerFactory.class);
    //
    private final MethodExecutorFactory methodExecutorFactory;
    private final Map<String, ImplicitParameterProvider> implicitParameterProviderMap;

    public InvocationHandlerFactory(MethodExecutorFactory methodExecutorFactory, Map<String, ImplicitParameterProvider> implicitParameterProviderMap) {
        Objects.requireNonNull(implicitParameterProviderMap);
        this.methodExecutorFactory = methodExecutorFactory;
        this.implicitParameterProviderMap = implicitParameterProviderMap;
    }

    public InvocationHandler createInvocationHandler(Class<?> clazz) {
        logger.debug("Creating invocation handler for class: {}", clazz);
        //
        final InterfaceContext interfaceContext = getInterfaceContext(clazz);
        //
        Map<Method, MethodInvocationHandler.MethodExecutionContext<?>> methodExecutionContextMap = new HashMap<>();
        final Method[] methods = clazz.getMethods();
        for(Method method: methods) {
            final MethodContext methodContext = getMethodContext(interfaceContext, method);
            methodExecutionContextMap.put(method, getMethodExecutionContext(methodContext));
        }

        return new MethodInvocationHandler(methodExecutionContextMap, implicitParameterProviderMap);
    }

    private <T> MethodInvocationHandler.MethodExecutionContext<T> getMethodExecutionContext(MethodContext methodContext) {
        final MethodExecutor<T> requestExecutor = methodExecutorFactory.getRequestExecutor(methodContext);
        return new MethodInvocationHandler.MethodExecutionContext<>(requestExecutor, methodContext.indexToParamMap(), methodContext.constImplicitParams(), methodContext.providedImplicitParams());
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
        final Map<String, String> constImplicitParams = new HashMap<>();
        final Map<String, String> providedImplicitParams = new HashMap<>();
        //
        final List<ImplicitParam> implicitParamList;
        final ImplicitParams implicitParams = method.getAnnotation(ImplicitParams.class);
        if(implicitParams != null) {
            implicitParamList = Arrays.asList(implicitParams.value());
        } else {
            final ImplicitParam implicitParam = method.getAnnotation(ImplicitParam.class);
            if(implicitParam != null) {
                final ArrayList<ImplicitParam> list = new ArrayList<>();
                list.add(implicitParam);
                implicitParamList = list;
            } else {
                implicitParamList = new ArrayList<>();
            }
        }

        for(ImplicitParam implicitParam : implicitParamList) {
            if(!"".equals(implicitParam.constValue()) && !"".equals(implicitParam.providerName())) {
                throw new IllegalStateException("ImplicitParam cannot simultaneously provide constValue and providerName");
            }
            if(!"".equals(implicitParam.constValue())) {
                constImplicitParams.put(implicitParam.paramName(), implicitParam.constValue());
            } else if (!"".equals(implicitParam.providerName())) {
                providedImplicitParams.put(implicitParam.paramName(), implicitParam.providerName());
            }

        }
        //
        return new DefaultMethodContext(interfaceContext, url, httpMethod, params, indexToNameMap, returnType, constImplicitParams, providedImplicitParams);
    }
}
