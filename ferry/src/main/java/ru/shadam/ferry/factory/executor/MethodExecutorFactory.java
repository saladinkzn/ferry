package ru.shadam.ferry.factory.executor;

import ru.shadam.ferry.analyze.MethodContext;

/**
 * Factory interface to construct new MethodExecutors
 *
 * @author sala
 *
 * @see MethodContext
 * @see MethodExecutor
 */
public interface MethodExecutorFactory {
    /**
     * Returns method executor for given method context
     */
    MethodExecutor getRequestExecutor(MethodContext methodContext);
}
