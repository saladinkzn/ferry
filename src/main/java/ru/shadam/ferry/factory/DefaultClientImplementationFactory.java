package ru.shadam.ferry.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import ru.shadam.ferry.factory.executor.HttpClientMethodExecutorFactory;
import ru.shadam.ferry.factory.responsehandler.ObjectMapperResponseHandlerFactory;

/**
 * @author sala
 */
public class DefaultClientImplementationFactory extends ClientImplementationFactory {
    public DefaultClientImplementationFactory(HttpClient httpClient, ObjectMapper objectMapper) {
        super(new HttpClientMethodExecutorFactory(httpClient, new ObjectMapperResponseHandlerFactory(objectMapper)));
    }
}
