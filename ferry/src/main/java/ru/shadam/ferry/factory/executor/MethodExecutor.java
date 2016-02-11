package ru.shadam.ferry.factory.executor;

import ru.shadam.ferry.factory.response.ResponseWrapper;

import java.io.IOException;
import java.util.Map;

/**
 * Http request executing and response parsing abstraction.
 *
 * @author sala
 * @see MethodExecutorFactory
 */
public interface MethodExecutor {
    /**
     * Execute http request with provided parameters
     *
     * @param parameters Map (Parameter name -&gt; Parameter value), cannot be null
     * @param pathVariables Map (Path variable name -&gt; Path variable value, cannot be null
     * @param requestBody Optional request body, can be null
     * @return Parsed response as java object
     * @throws IOException
     */
    ResponseWrapper execute(Map<String, ?> parameters, Map<String, ?> pathVariables, String requestBody) throws IOException;
}
