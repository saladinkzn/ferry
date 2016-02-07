package ru.shadam.ferry.factory.executor;

import java.io.IOException;
import java.util.Map;

/**
 * @author sala
 */
public interface MethodExecutor<T> {
    T execute(Map<String, ?> parameters, Map<String, ?> pathVariables) throws IOException;
}
