package ru.shadam.restclient.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import ru.shadam.restclient.factory.executor.HttpClientMethodExecutorFactory;
import ru.shadam.restclient.factory.responsehandler.ObjectMapperResponseHandlerFactory;

/**
 * @author sala
 */
public class DefaultClientImplementationFactory extends ClientImplementationFactory {
    public DefaultClientImplementationFactory(HttpClient httpClient, ObjectMapper objectMapper) {
        super(new HttpClientMethodExecutorFactory(httpClient, new ObjectMapperResponseHandlerFactory(objectMapper)));
    }
}
