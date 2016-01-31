package ru.shadam.restclient.factory;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author sala
 */
class MethodExecutor<T> {
    private final HttpClient httpClient;
    private final ResponseHandler<T> responseHandler;

    private final String method;
    private final String url;
    private final Set<String> parameters;
    private final Map<Integer, String> indexToNameMap;
    private final Map<String, String> constImplicitParameters;
    //


    public T execute(Object[] args) throws IOException {
        final Map<String, Object> paramToValueMap = new HashMap<>();
        for (final Map.Entry<Integer, String> indexToParam : indexToNameMap.entrySet()) {
            final Integer index = indexToParam.getKey();
            final String paramName = indexToParam.getValue();
            paramToValueMap.put(paramName, args[index]);
        }
        return execute(paramToValueMap);
    }

    public MethodExecutor(HttpClient httpClient, String method, Set<String> parameters, String url, ResponseHandler<T> responseHandler, Map<Integer, String> indexToNameMap, Map<String, String> constImplicitParameters) {
        this.constImplicitParameters = Objects.requireNonNull(constImplicitParameters);
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
        for(Map.Entry<String, String> constImplicitParameter : constImplicitParameters.entrySet()) {
            requestBuilder.addParameter(constImplicitParameter.getKey(), constImplicitParameter.getValue());
        }
        for(String parameterKey: parameters) {
            if(!values.containsKey(parameterKey)) {
                continue;
            }
            requestBuilder.addParameter(parameterKey, String.valueOf(values.get(parameterKey)));
        }
        return requestBuilder.build();
    }

}
