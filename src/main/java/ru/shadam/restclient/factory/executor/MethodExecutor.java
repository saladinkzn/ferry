package ru.shadam.restclient.factory.executor;

import java.io.IOException;
import java.util.Map;

/**
 * @author sala
 */
public interface MethodExecutor<T> {
    T execute(Map<String, ?> parameters) throws IOException;
}
