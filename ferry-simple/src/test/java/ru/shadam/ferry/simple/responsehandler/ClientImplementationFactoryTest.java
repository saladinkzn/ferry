package ru.shadam.ferry.simple.responsehandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import org.apache.http.client.HttpClient;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import ru.shadam.ferry.analyze.MethodContext;
import ru.shadam.ferry.factory.response.ResponseWrapper;
import ru.shadam.ferry.factory.result.ResultExtractor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author sala
 */
public class ClientImplementationFactoryTest {
    @Test
    public void testResponseHandlerFactory() throws IOException {
        final HttpClient httpClient = Mockito.mock(HttpClient.class);
        final ObjectMapperResponseHandlerFactory objectMapperResponseHandlerFactory = new ObjectMapperResponseHandlerFactory(new ObjectMapper());
        final MethodContext methodContext = Mockito.mock(MethodContext.class);
        Mockito.when(methodContext.returnType()).thenReturn(new TypeToken<List<Integer>>() {}.getType());
        final ResultExtractor<List<Integer>> listResponseHandler = objectMapperResponseHandlerFactory.getResultExtractor(methodContext);
        //
        final ResponseWrapper httpResponse = Mockito.mock(ResponseWrapper.class);
        //
        final String responseJson = "[ 1, 2, 3]";
        final InputStream byteArrayInputStream = new ByteArrayInputStream(responseJson.getBytes(StandardCharsets.UTF_8));
        Mockito.when(httpResponse.getInputStream()).thenReturn(byteArrayInputStream);
        //
        final List<Integer> list = listResponseHandler.extractResponse(httpResponse);
        Assert.assertNotNull(list);
        Assert.assertEquals(3L, list.size());
        Assert.assertEquals(1, list.get(0).longValue());
        Assert.assertEquals(2, list.get(1).longValue());
        Assert.assertEquals(3, list.get(2).longValue());
    }
}