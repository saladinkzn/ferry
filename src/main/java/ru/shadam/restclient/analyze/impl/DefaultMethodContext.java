package ru.shadam.restclient.analyze.impl;

import ru.shadam.restclient.analyze.InterfaceContext;
import ru.shadam.restclient.analyze.MethodContext;

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

    public DefaultMethodContext(InterfaceContext interfaceContext, String url, String method, LinkedHashSet<String> params, Map<Integer, String> indexToParamMap, Type returnType) {
        this.url = url;
        this.method = method;
        this.interfaceContext = Objects.requireNonNull(interfaceContext);
        this.params = Objects.requireNonNull(params);
        this.indexToParamMap = Objects.requireNonNull(indexToParamMap);
        this.returnType = Objects.requireNonNull(returnType);
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
}
