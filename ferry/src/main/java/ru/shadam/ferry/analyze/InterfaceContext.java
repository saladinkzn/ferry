package ru.shadam.ferry.analyze;

import java.util.Map;

/**
 * @author sala
 */
public interface InterfaceContext {
    String baseUrl();

    String defaultMethod();

    Map<String,String> constImplicitParams();

    Map<String,String> providedImplicitParams();
}
