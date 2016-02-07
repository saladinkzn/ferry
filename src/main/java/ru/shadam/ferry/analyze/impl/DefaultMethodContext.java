package ru.shadam.ferry.analyze.impl;

import ru.shadam.ferry.analyze.InterfaceContext;
import ru.shadam.ferry.analyze.MethodContext;

import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;

/**
 * @author sala
 */
public class DefaultMethodContext implements MethodContext {
    private final InterfaceContext interfaceContext;
    private String url;
    private String method;
    private final LinkedHashSet<String> params;
    private final Map<Integer,String> indexToParamMap;
    private final Type returnType;
    //
    private final Map<String, String> constImplicitParams;
    private final Map<String, String> providedImplicitParams;
    private final Map<Integer, String> indexToPathVariableMap;

    public DefaultMethodContext(InterfaceContext interfaceContext, String url, String method, LinkedHashSet<String> params, Map<Integer, String> indexToParamMap, Type returnType, Map<String, String> constImplicitParams, Map<String, String> providedImplicitParams, Map<Integer, String> indexToPathVariableMap) {
        this.url = url;
        this.method = method;
        this.indexToPathVariableMap = indexToPathVariableMap;
        this.interfaceContext = Objects.requireNonNull(interfaceContext);
        this.params = Objects.requireNonNull(params);
        this.indexToParamMap = Objects.requireNonNull(indexToParamMap);
        this.returnType = Objects.requireNonNull(returnType);
        this.constImplicitParams = Objects.requireNonNull(constImplicitParams);
        this.providedImplicitParams = Objects.requireNonNull(providedImplicitParams);
    }

    @Override
    public String url() {
        return interfaceContext.baseUrl() + (url != null ? url : "");
    }

    @Override
    public String method() {
        return method == null ? interfaceContext.defaultMethod() : method;
    }

    @Override
    public LinkedHashSet<String> params() {
        return params;
    }

    @Override
    public Map<Integer, String> indexToParamMap() {
        return indexToParamMap;
    }

    @Override
    public Type returnType() {
        return returnType;
    }

    @Override
    public Map<String, String> constImplicitParams() {
        return constImplicitParams;
    }

    @Override
    public Map<String, String> providedImplicitParams() {
        return providedImplicitParams;
    }

    @Override
    public Map<Integer, String> indexToPathVariableMap() {
        return indexToPathVariableMap;
    }

    @Override
    public String toString() {
        return "DefaultMethodContext{" +
                "interfaceContext=" + interfaceContext +
                ", url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", params=" + params +
                ", indexToParamMap=" + indexToParamMap +
                ", returnType=" + returnType +
                ", constImplicitParams=" + constImplicitParams +
                ", providedImplicitParams=" + providedImplicitParams +
                '}';
    }
}
