package ru.shadam.ferry.factory.executor;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shadam.ferry.analyze.MethodContext;
import ru.shadam.ferry.factory.responsehandler.ResponseHandlerFactory;

import java.util.Objects;

/**
 * @author sala
 */
public class HttpClientMethodExecutorFactory implements MethodExecutorFactory {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientMethodExecutorFactory.class);

    private final HttpClient httpClient;
    private final ResponseHandlerFactory responseHandlerFactory;

    public HttpClientMethodExecutorFactory(HttpClient httpClient, ResponseHandlerFactory responseHandlerFactory) {
        this.httpClient = Objects.requireNonNull(httpClient);
        this.responseHandlerFactory = Objects.requireNonNull(responseHandlerFactory);
    }

    @Override
    public <T> MethodExecutor<T> getRequestExecutor(MethodContext methodContext) {
        Objects.requireNonNull(methodContext);
        //
        logger.debug("Getting request executor for method context: {}", methodContext);
        //
        final String url = methodContext.url();
        final String method = methodContext.method();
        //
        final ResponseHandler<T> responseHandler = responseHandlerFactory.getResponseHandler(methodContext.returnType());

        return new HttpClientMethodExecutor<T>(httpClient, method, url, responseHandler);
    }
}
