package ru.shadam.ferry.analyze.impl;

import ru.shadam.ferry.analyze.InterfaceContext;

/**
 * @author sala
 */
public class DefaultInterfaceContext implements InterfaceContext {
    private String baseUrl;
    private String defaultMethod;

    public DefaultInterfaceContext(String baseUrl, String defaultMethod) {
        this.baseUrl = baseUrl;
        this.defaultMethod = defaultMethod;
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
    public String toString() {
        return "DefaultInterfaceContext{" +
                "baseUrl='" + baseUrl + '\'' +
                ", defaultMethod='" + defaultMethod + '\'' +
                '}';
    }
}
