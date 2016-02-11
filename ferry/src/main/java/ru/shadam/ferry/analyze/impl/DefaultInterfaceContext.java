package ru.shadam.ferry.analyze.impl;

import ru.shadam.ferry.analyze.InterfaceContext;

import java.util.Map;

/**
 * @author sala
 */
public class DefaultInterfaceContext implements InterfaceContext {
    private final String baseUrl;
    private final String defaultMethod;
    private final Map<String, String> constImplicitParams;
    private final Map<String, String> providedImplicitParams;
    private final Class<?> interfaceType;

    public DefaultInterfaceContext(String baseUrl, String defaultMethod, Map<String, String> constImplicitParams, Map<String, String> providedImplicitParams, Class<?> interfaceType) {
        this.baseUrl = baseUrl;
        this.defaultMethod = defaultMethod;
        this.constImplicitParams = constImplicitParams;
        this.providedImplicitParams = providedImplicitParams;
        this.interfaceType = interfaceType;
    }

    @Override
    public String baseUrl() {
        return baseUrl;
    }

    @Override
    public String defaultMethod() {
        return defaultMethod;
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
    public Class<?> interfaceType() {
        return interfaceType;
    }


    @Override
    public String toString() {
        return "DefaultInterfaceContext{" +
                "baseUrl='" + baseUrl + '\'' +
                ", defaultMethod='" + defaultMethod + '\'' +
                '}';
    }
}
