package ru.shadam.restclient.factory;

import org.apache.http.client.ResponseHandler;

import java.util.Map;
import java.util.Set;

/**
 * @author sala
 */
public interface ExecutionHelperFactory {
    <T> ExecutionHelper<T> getRequestExecutor(String method, String url, Set<String> params, Map<Integer, String> indexToParamNameMap, ResponseHandler<T> responseHandler);
}
