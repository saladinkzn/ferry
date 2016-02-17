package ru.shadam.ferry.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shadam.ferry.analyze.InterfaceContext;
import ru.shadam.ferry.analyze.MethodContext;
import ru.shadam.ferry.analyze.impl.DefaultInterfaceContext;
import ru.shadam.ferry.analyze.impl.DefaultMethodContext;
import ru.shadam.ferry.annotations.*;
import ru.shadam.ferry.factory.converter.RequestBodyConverter;
import ru.shadam.ferry.factory.executor.MethodExecutor;
import ru.shadam.ferry.factory.executor.MethodExecutorFactory;
import ru.shadam.ferry.factory.result.ResultExtractor;
import ru.shadam.ferry.factory.result.ResultExtractorFactory;
import ru.shadam.ferry.factory.result.UnsupportedTypeException;
import ru.shadam.ferry.implicit.ImplicitParameterProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author sala
 */
public class InvocationHandlerFactory {
    private static final Logger logger = LoggerFactory.getLogger(InvocationHandlerFactory.class);
    public static final String DUPLICATE_MAP_PARAM_MESSAGE = "Duplicate @Param was found on Map instance. Only one Map @Param is allowed";
    //
    private final MethodExecutorFactory methodExecutorFactory;
    private final ResultExtractorFactory resultExtractorFactory;
    //
    private final Map<String, ImplicitParameterProvider> implicitParameterProviderMap;
    private final RequestBodyConverter requestBodyConverter;

    @Deprecated
    public InvocationHandlerFactory(MethodExecutorFactory methodExecutorFactory,
                                    ResultExtractorFactory resultExtractorFactory,
                                    Map<String, ImplicitParameterProvider> implicitParameterProviderMap) {
        this(methodExecutorFactory, resultExtractorFactory, implicitParameterProviderMap, new RequestBodyConverter() {
            @Override
            public <T> String convert(T value) {
                return String.valueOf(value);
            }

            @Override
            public <T> boolean canConvert(Class<T> clazz) {
                return true;
            }
        });
    }

    public InvocationHandlerFactory(MethodExecutorFactory methodExecutorFactory,
                                    ResultExtractorFactory resultExtractorFactory,
                                    Map<String, ImplicitParameterProvider> implicitParameterProviderMap,
                                    RequestBodyConverter requestBodyConverter) {
        this.resultExtractorFactory = resultExtractorFactory;
        Objects.requireNonNull(methodExecutorFactory);
        Objects.requireNonNull(implicitParameterProviderMap);
        Objects.requireNonNull(requestBodyConverter);
        this.methodExecutorFactory = methodExecutorFactory;
        this.implicitParameterProviderMap = implicitParameterProviderMap;
        this.requestBodyConverter = requestBodyConverter;
    }

    // TODO: collect exception for all methods and throw an uberexception about whole interface
    public InvocationHandler createInvocationHandler(Class<?> clazz) throws UnsupportedTypeException {
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

        return new MethodInvocationHandler(methodExecutionContextMap, implicitParameterProviderMap, requestBodyConverter);
    }

    private <T> MethodInvocationHandler.MethodExecutionContext<T> getMethodExecutionContext(MethodContext methodContext) throws UnsupportedTypeException {
        final MethodExecutor requestExecutor = methodExecutorFactory.getRequestExecutor(methodContext);
        final ResultExtractor<T> resultExtractor = resultExtractorFactory.getResultExtractor(methodContext);
        return new MethodInvocationHandler.MethodExecutionContext<>(requestExecutor, resultExtractor,
                methodContext.indexToParamMap(),
                methodContext.constImplicitParams(),
                methodContext.providedImplicitParams(),
                methodContext.indexToPathVariableMap(),
                methodContext.requestBodyIndex(),
                methodContext.mapParameterIndex()
        );
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
        final List<ImplicitParam> implicitParamList = parseImplicitParams(
                clazz.getAnnotation(ImplicitParams.class),
                clazz.getAnnotation(ImplicitParam.class)
        );
        final Map<String, String> constImplicitParams = new HashMap<>();
        final Map<String, String> providedImplicitParams = new HashMap<>();
        fillImplicitParamMaps(implicitParamList, constImplicitParams, providedImplicitParams);
        return new DefaultInterfaceContext(baseUrl, defaultMethod, constImplicitParams, providedImplicitParams, clazz);
    }

    static MethodContext getMethodContext(InterfaceContext interfaceContext, Method method) {
        Type returnType = processMethodType(method, interfaceContext.interfaceType());
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
        final Map<Integer, String> indexToPathVariableMap = new HashMap<>();
        final Type[] genericParameterTypes = method.getGenericParameterTypes();
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Integer requestBodyIndex = null;
        Integer mapParameterIndex = null;
        for(int paramIndex = 0; paramIndex < parameterAnnotations.length; paramIndex++) {
            final Annotation[] parameterAnnotationArray = parameterAnnotations[paramIndex];
            final Type genericParameterType = genericParameterTypes[paramIndex];
            for(Annotation annotation : parameterAnnotationArray) {
                final Class<? extends Annotation> annotationType = annotation.annotationType();
                if(annotationType.isAssignableFrom(Param.class)) {
                    final Param param = ((Param) annotation);
                    final String paramName = param.value();
                    if("".equals(paramName)) {
                        if(genericParameterType instanceof Class<?>) {
                            if(Map.class.isAssignableFrom(((Class) genericParameterType))) {
                                if(mapParameterIndex == null) {
                                    mapParameterIndex = paramIndex;
                                } else {
                                    logger.warn(DUPLICATE_MAP_PARAM_MESSAGE);
                                }
                            }
                        } else if (genericParameterType instanceof ParameterizedType) {
                            if(Map.class.isAssignableFrom(((Class<?>) ((ParameterizedType) genericParameterType).getRawType()))) {
                                if(mapParameterIndex == null) {
                                    mapParameterIndex = paramIndex;
                                } else {
                                    logger.warn(DUPLICATE_MAP_PARAM_MESSAGE);
                                }
                            }
                        } else {
                            throw new IllegalStateException("Empty parameter name is supported only for classes derived from Map");
                        }
                    } else {
                        params.add(paramName);
                        indexToNameMap.put(paramIndex, paramName);
                    }
                    break;
                } else if(annotationType.isAssignableFrom(PathVariable.class)) {
                    final PathVariable pathVariable = ((PathVariable) annotation);
                    final String pathVariableName = pathVariable.value();
                    indexToPathVariableMap.put(paramIndex, pathVariableName);
                    break;
                } else if(annotationType.isAssignableFrom(RequestBody.class)) {
                    if (requestBodyIndex == null) {
                        requestBodyIndex = paramIndex;
                    }
                }
            }
        }
        //
        final List<ImplicitParam> implicitParamList = parseImplicitParams(
                method.getAnnotation(ImplicitParams.class), method.getAnnotation(ImplicitParam.class)
        );

        final Map<String, String> constImplicitParams = new HashMap<>(interfaceContext.constImplicitParams());
        final Map<String, String> providedImplicitParams = new HashMap<>(interfaceContext.providedImplicitParams());
        fillImplicitParamMaps(implicitParamList, constImplicitParams, providedImplicitParams);
        //
        return new DefaultMethodContext(interfaceContext, url, httpMethod, params, indexToNameMap, returnType, constImplicitParams, providedImplicitParams, indexToPathVariableMap, requestBodyIndex, mapParameterIndex);
    }

    private static Type processMethodType(Method method, Class<?> context) {
        final Type genericReturnType = method.getGenericReturnType();
        if(genericReturnType instanceof Class<?>) {
            return genericReturnType;
        }
        if(genericReturnType instanceof ParameterizedType) {
            final Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
            boolean allMatch = true;
            for(Type typeArgument : actualTypeArguments) {
                if(typeArgument instanceof TypeVariable) {
                    allMatch = false;
                    break;
                }
            }
            if(allMatch) {
                return genericReturnType;
            }
        }
        //
        Stack<Type> stack = new Stack<>();
        stack.push(context);
        while (!stack.isEmpty()) {
            final Type type = stack.pop();
            if(type instanceof Class<?>) {
                try {
                    final Method declaredMethod = ((Class) type).getDeclaredMethod(method.getName(), method.getParameterTypes());
                    throw new IllegalStateException("Raw class contains parametrized return type, fail");
                } catch (NoSuchMethodException nsme) {
                    for(Type t: context.getGenericInterfaces()) {
                        stack.push(t);
                    }
                }
            } else if (type instanceof ParameterizedType) {
                final Class<?> rawType = (Class<?>) ((ParameterizedType) type).getRawType();
                try {
                    final Method declaredMethod = rawType.getDeclaredMethod(method.getName(), method.getParameterTypes());
                    if(genericReturnType instanceof ParameterizedType) {
                        return processParameterizedType(((ParameterizedType) genericReturnType), ((ParameterizedType) type));
                    } else if (genericReturnType instanceof TypeVariable) {
                        return processTypeVariable(((TypeVariable) genericReturnType), ((ParameterizedType) type));
                    } else {
                        throw new IllegalStateException("Unknown genericReturnType: " + genericReturnType.getClass());
                    }
                } catch (NoSuchMethodException e) {
                    for(Type t: rawType.getGenericInterfaces()) {
                        stack.push(t);
                    }
                }
            }

        }
        throw new IllegalStateException("No interface of context declares method");
    }

    private static Type processTypeVariable(TypeVariable typeVariable, ParameterizedType declaringType) {
        final Map<String, Type> stringClassMap = typeVariableToClassMap(declaringType);
        return stringClassMap.get(typeVariable.getName());
    }

    private static ParameterizedType processParameterizedType(ParameterizedType returnType, ParameterizedType declaringType) {
        final Map<String, Type> typeVariableClassMap = typeVariableToClassMap(declaringType);
        //
        final Type[] array = returnType.getActualTypeArguments();
        final Type[] resolved = new Type[array.length];
        for (int i = 0; i < array.length; i++) {
            Type typeArgument = array[i];
            final Type val;
            if(typeArgument instanceof TypeVariable) {
                final String name = ((TypeVariable) typeArgument).getName();
                val = typeVariableClassMap.get(name);
            } else {
                val = typeArgument;
            }
            resolved[i] = val;
        }
        return new MyParameterizedType(resolved, returnType);
    }

    private static Map<String, Type> typeVariableToClassMap(ParameterizedType declaringType) {
        final Map<String, Type> typeVariableClassMap = new HashMap<>();
        final Type[] actualTypeArguments = declaringType.getActualTypeArguments();
        final TypeVariable<?>[] typeParameters = ((Class<?>) declaringType.getRawType()).getTypeParameters();
        assert actualTypeArguments.length == typeParameters.length;
        for(int i = 0; i < actualTypeArguments.length; i++) {
            typeVariableClassMap.put(typeParameters[i].getName(), actualTypeArguments[i]);
        }
        return typeVariableClassMap;
    }

    private static List<ImplicitParam> parseImplicitParams(ImplicitParams implicitParams, ImplicitParam implicitParam) {
        final List<ImplicitParam> implicitParamList;
        if(implicitParams != null) {
            implicitParamList = Arrays.asList(implicitParams.value());
        } else {
            if(implicitParam != null) {
                final ArrayList<ImplicitParam> list = new ArrayList<>();
                list.add(implicitParam);
                implicitParamList = list;
            } else {
                implicitParamList = new ArrayList<>();
            }
        }
        return implicitParamList;
    }

    private static void fillImplicitParamMaps(List<ImplicitParam> implicitParamList, Map<String, String> constImplicitParams, Map<String, String> providedImplicitParams) {
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
    }

    private static class MyParameterizedType implements ParameterizedType {
        private final Type[] resolved;
        private final ParameterizedType returnType;

        public MyParameterizedType(Type[] resolved, ParameterizedType returnType) {
            this.resolved = resolved;
            this.returnType = returnType;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return resolved;
        }

        @Override
        public Type getRawType() {
            return returnType.getRawType();
        }

        @Override
        public Type getOwnerType() {
            return returnType.getOwnerType();
        }
    }
}
