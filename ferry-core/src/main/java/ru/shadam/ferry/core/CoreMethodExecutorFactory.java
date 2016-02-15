package ru.shadam.ferry.core;

import ru.shadam.ferry.analyze.MethodContext;
import ru.shadam.ferry.factory.executor.MethodExecutor;
import ru.shadam.ferry.factory.executor.MethodExecutorFactory;

/**
 * @author timur.shakurov@dz.ru
 */
public class CoreMethodExecutorFactory implements MethodExecutorFactory {
    @Override
    public MethodExecutor getRequestExecutor(MethodContext methodContext) {
        final String url = methodContext.url();
        final String method = methodContext.method();
        return new CoreMethodExecutor(url, method);
    }
}
