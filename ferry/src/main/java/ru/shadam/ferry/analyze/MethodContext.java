package ru.shadam.ferry.analyze;

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

    Map<String, String> constImplicitParams();

    Map<String, String> providedImplicitParams();

    Map<Integer,String> indexToPathVariableMap();

    Integer requestBodyIndex();
}
