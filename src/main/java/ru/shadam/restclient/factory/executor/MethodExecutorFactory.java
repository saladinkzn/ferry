package ru.shadam.restclient.factory.executor;

import ru.shadam.restclient.analyze.MethodContext;

/**
 * @author sala
 */
public interface MethodExecutorFactory {
    <T> MethodExecutor<T> getRequestExecutor(MethodContext methodContext);
}
