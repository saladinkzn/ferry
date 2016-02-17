package ru.shadam.ferry.analyze.impl;

import ru.shadam.ferry.analyze.InterfaceContext;
import ru.shadam.ferry.analyze.MethodContext;
import ru.shadam.ferry.util.MoreObjects;

import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Map;
import ru.shadam.ferry.util.MoreObjects;

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
    private final Integer requestBodyIndex;
    private final Integer mapParameterIndex;
    private Integer beanParameterIndex;

    public DefaultMethodContext(InterfaceContext interfaceContext, String url, String method, LinkedHashSet<String> params, Map<Integer, String> indexToParamMap, Type returnType, Map<String, String> constImplicitParams, Map<String, String> providedImplicitParams, Map<Integer, String> indexToPathVariableMap, Integer requestBodyIndex, Integer mapParameterIndex, Integer beanParameterIndex) {
        this.url = url;
        this.method = method;
        this.indexToPathVariableMap = indexToPathVariableMap;
        this.beanParameterIndex = beanParameterIndex;
        this.interfaceContext = MoreObjects.requireNonNull(interfaceContext);
        this.params = MoreObjects.requireNonNull(params);
        this.indexToParamMap = MoreObjects.requireNonNull(indexToParamMap);
        this.returnType = MoreObjects.requireNonNull(returnType);
        this.constImplicitParams = MoreObjects.requireNonNull(constImplicitParams);
        this.providedImplicitParams = MoreObjects.requireNonNull(providedImplicitParams);
        this.requestBodyIndex = requestBodyIndex;
        this.mapParameterIndex = mapParameterIndex;
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
    public Integer requestBodyIndex() {
        return requestBodyIndex;
    }

    @Override
    public Integer mapParameterIndex() {
        return mapParameterIndex;
    }

    @Override
    public Integer beanParameterIndex() {
        return beanParameterIndex;
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
                ", requestBodyIndex=" + requestBodyIndex +
                '}';
    }
}
