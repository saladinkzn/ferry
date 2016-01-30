package ru.shadam.restclient.analyze.impl;

import ru.shadam.restclient.analyze.InterfaceContext;

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
}
