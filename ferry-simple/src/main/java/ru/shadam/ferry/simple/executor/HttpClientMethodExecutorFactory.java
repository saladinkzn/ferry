package ru.shadam.ferry.simple.executor;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shadam.ferry.analyze.MethodContext;
import ru.shadam.ferry.factory.executor.MethodExecutor;
import ru.shadam.ferry.factory.executor.MethodExecutorFactory;

import java.util.Objects;

/**
 * @author sala
 */
public class HttpClientMethodExecutorFactory implements MethodExecutorFactory {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientMethodExecutorFactory.class);

    private final HttpClient httpClient;

    public HttpClientMethodExecutorFactory(HttpClient httpClient) {
        this.httpClient = Objects.requireNonNull(httpClient);
    }

    @Override
    public MethodExecutor getRequestExecutor(MethodContext methodContext) {
        Objects.requireNonNull(methodContext);
        //
        logger.debug("Getting request executor for method context: {}", methodContext);
        //
        final String url = methodContext.url();
        final String method = methodContext.method();
        //
        return new HttpClientMethodExecutor(httpClient, method, url);
    }
}
