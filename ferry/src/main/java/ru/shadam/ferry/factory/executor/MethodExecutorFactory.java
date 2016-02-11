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
     *
     * @param methodContext context of method to create request executor for
     * @return MethodExecutor to execute request
     */
    MethodExecutor getRequestExecutor(MethodContext methodContext);
}
