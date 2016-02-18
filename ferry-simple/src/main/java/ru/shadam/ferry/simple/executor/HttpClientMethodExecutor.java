package ru.shadam.ferry.simple.executor;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shadam.ferry.factory.executor.MethodExecutor;
import ru.shadam.ferry.factory.response.DefaultResponseWrapper;
import ru.shadam.ferry.factory.response.ResponseWrapper;

import java.io.IOException;
import java.util.Map;
import ru.shadam.ferry.util.MoreObjects;
import ru.shadam.ferry.util.MoreStandardCharsetes;

/**
 * @author sala
 */
public class HttpClientMethodExecutor implements MethodExecutor {
    // TODO: use this logger u bastard
    private static final Logger logger = LoggerFactory.getLogger(HttpClientMethodExecutor.class);
    private final HttpClient httpClient;
    private final String method;
    private final String url;


    public HttpClientMethodExecutor(HttpClient httpClient, String method, String url) {
        this.httpClient = MoreObjects.requireNonNull(httpClient);
        this.method = MoreObjects.requireNonNull(method);
        this.url = MoreObjects.requireNonNull(url);
    }

    @Override
    public ResponseWrapper execute(Map<String, ?> parameters, Map<String, ?> pathVariables, String requestBody) throws IOException {
        final HttpUriRequest request = getHttpUriRequest(parameters, pathVariables, requestBody);
        logger.debug("Executing request: {} {}", request.getMethod(), request.getURI());
        return convert(httpClient.execute(request));

    }

    private ResponseWrapper convert(HttpResponse execute) throws IOException {
        if(execute == null) {
            // TODO: ???
            return null;
        }
        return new DefaultResponseWrapper(execute.getEntity().getContent());
    }

    HttpUriRequest getHttpUriRequest(Map<String, ?> values, Map<String, ?> pathVariables) {
        return getHttpUriRequest(values, pathVariables, null);
    }

    HttpUriRequest getHttpUriRequest(Map<String, ?> values, Map<String, ?> pathVariables, String requestBody) {
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
        if(requestBody != null) {
            final StringEntity stringEntity = new StringEntity(requestBody, MoreStandardCharsetes.UTF_8);
            requestBuilder.setEntity(stringEntity);
        }
        return requestBuilder.build();
    }


    @Override
    public String toString() {
        return "MethodExecutor{" +
                "method='" + method + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
