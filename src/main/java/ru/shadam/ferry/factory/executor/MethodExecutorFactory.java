package ru.shadam.ferry.factory.executor;

import ru.shadam.ferry.analyze.MethodContext;

/**
 * @author sala
 */
public interface MethodExecutorFactory {
    <T> MethodExecutor<T> getRequestExecutor(MethodContext methodContext);
}
