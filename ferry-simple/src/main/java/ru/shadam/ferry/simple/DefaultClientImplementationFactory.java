package ru.shadam.ferry.simple;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import ru.shadam.ferry.factory.ClientImplementationFactory;
import ru.shadam.ferry.factory.converter.CompositeRequestBodyConverter;
import ru.shadam.ferry.factory.converter.StringRequestBodyConverter;
import ru.shadam.ferry.factory.result.CompositeResultExtractorFactory;
import ru.shadam.ferry.factory.result.VoidResultExtractorFactory;
import ru.shadam.ferry.simple.converter.ObjectMapperRequestBodyConverter;
import ru.shadam.ferry.simple.executor.HttpClientMethodExecutorFactory;
import ru.shadam.ferry.simple.responsehandler.ObjectMapperResponseHandlerFactory;

import java.util.Arrays;

/**
 * Default Apache HttpClient and Jackson-based implementation of {@link ClientImplementationFactory}
 * @author sala
 *
 * @see ClientImplementationFactory
 */
public class DefaultClientImplementationFactory extends ClientImplementationFactory {
    public DefaultClientImplementationFactory(HttpClient httpClient, ObjectMapper objectMapper) {
        super(
                new HttpClientMethodExecutorFactory(httpClient),
                new CompositeResultExtractorFactory(
                        Arrays.asList(
                                new VoidResultExtractorFactory(),
                                new ObjectMapperResponseHandlerFactory(objectMapper)
                        )
                ),
                new CompositeRequestBodyConverter(Arrays.asList(new StringRequestBodyConverter(), new ObjectMapperRequestBodyConverter(objectMapper)))
        );
    }
}
