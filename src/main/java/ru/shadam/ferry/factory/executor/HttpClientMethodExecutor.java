package ru.shadam.ferry.factory.executor;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * @author sala
 */
public class HttpClientMethodExecutor<T> implements MethodExecutor<T> {
    // TODO: use this logger u bastard
    private static final Logger logger = LoggerFactory.getLogger(HttpClientMethodExecutor.class);
    private final HttpClient httpClient;
    private final String method;
    private final String url;
    private final ResponseHandler<T> responseHandler;


    public HttpClientMethodExecutor(HttpClient httpClient, String method, String url, ResponseHandler<T> responseHandler) {
        this.httpClient = Objects.requireNonNull(httpClient);
        this.method = Objects.requireNonNull(method);
        this.url = Objects.requireNonNull(url);
        this.responseHandler = Objects.requireNonNull(responseHandler);
    }

    @Override
    public T execute(Map<String, ?> parameters, Map<String, ?> pathVariables) throws IOException {
        final HttpUriRequest request = getHttpUriRequest(parameters, pathVariables);
        logger.debug("Executing request: {} {}", request.getMethod(), request.getURI());
        return httpClient.execute(request, responseHandler);

    }

    public HttpUriRequest getHttpUriRequest(Map<String, ?> values, Map<String, ?> pathVariables) {
        final RequestBuilder requestBuilder = RequestBuilder.create(method);
        String replaceCandidate = url;
        for(Map.Entry<String, ?> pathVariableEntry: pathVariables.entrySet()) {
            final String pathVariableName = pathVariableEntry.getKey();
            final String pathVariableValue = String.valueOf(pathVariableEntry.getValue());
            replaceCandidate = replaceCandidate.replace(":" + pathVariableName, pathVariableValue);
        }
        requestBuilder.setUri(replaceCandidate);
        for(Map.Entry<String, ?> parameterEntry: values.entrySet()) {
            final String parameterKey = parameterEntry.getKey();
            requestBuilder.addParameter(parameterKey, String.valueOf(parameterEntry.getValue()));
        }
        return requestBuilder.build();
    }


    @Override
    public String toString() {
        return "MethodExecutor{" +
                "method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", responseHandler=" + responseHandler +
                '}';
    }
}
