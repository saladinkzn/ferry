package ru.shadam.ferry.factory.executor;

import ru.shadam.ferry.analyze.MethodContext;

/**
 * Factory interface to construct new MethodExecutors
 *
 * @author sala
 *
 * @see MethodContext
 * @see MethodExecutor
 * @see HttpClientMethodExecutorFactory
 */
public interface MethodExecutorFactory {
    /**
     * Returns method executor for given method context
     *
     * @param <T> Method's return type.
     */
    <T> MethodExecutor<T> getRequestExecutor(MethodContext methodContext);
}
