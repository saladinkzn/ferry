package ru.shadam.restclient.analyze;

import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @author sala
 */
public interface MethodContext {
    String url();

    String method();

    LinkedHashSet<String> params();

    Map<Integer, String> indexToParamMap();

    Type returnType();
}
