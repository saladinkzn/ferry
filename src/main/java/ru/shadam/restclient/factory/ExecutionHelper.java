package ru.shadam.restclient.factory;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author sala
 */
class ExecutionHelper<T> {
    private static final Logger logger = LoggerFactory.getLogger(ExecutionHelper.class);
    private final HttpClient httpClient;
    private final String method;
    private final String url;
    private final Set<String> parameters;
    private final ResponseHandler<T> responseHandler;
    //
    private final Map<Integer, String> indexToNameMap;


    public T execute(Object[] args) throws IOException {
        final Map<String, Object> paramToValueMap = new HashMap<>();
        for (final Map.Entry<Integer, String> indexToParam : indexToNameMap.entrySet()) {
            final Integer index = indexToParam.getKey();
            final String paramName = indexToParam.getValue();
            paramToValueMap.put(paramName, args[index]);
        }
        return execute(paramToValueMap);
    }

    public ExecutionHelper(HttpClient httpClient, String method, Set<String> parameters, String url, ResponseHandler<T> responseHandler, Map<Integer, String> indexToNameMap) {
        this.indexToNameMap = Objects.requireNonNull(indexToNameMap);
        this.httpClient = Objects.requireNonNull(httpClient);
        this.method = Objects.requireNonNull(method);
        this.parameters = Objects.requireNonNull(parameters);
        this.url = Objects.requireNonNull(url);
        this.responseHandler = Objects.requireNonNull(responseHandler);
    }

    // for tests mostly
    public T execute(Map<String, ?> values) throws IOException {
        final HttpUriRequest request = getHttpUriRequest(values);
        return httpClient.execute(request, responseHandler);

    }

    HttpUriRequest getHttpUriRequest(Map<String, ?> values) {
        final RequestBuilder requestBuilder = RequestBuilder.create(method);
        requestBuilder.setUri(url);
        for(String parameterKey: parameters) {
            if(!values.containsKey(parameterKey)) {
                continue;
            }
            requestBuilder.addParameter(parameterKey, String.valueOf(values.get(parameterKey)));
        }
        return requestBuilder.build();
    }

}
