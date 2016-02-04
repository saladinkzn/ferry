package ru.shadam.ferry.factory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import ru.shadam.ferry.factory.responsehandler.ObjectMapperResponseHandlerFactory;

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
        final ResponseHandler<List<Integer>> listResponseHandler = objectMapperResponseHandlerFactory.getResponseHandler(new TypeReference<List<Integer>>() {}.getType());
        //
        final HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        final HttpEntity httpEntity = Mockito.mock(HttpEntity.class);
        //
        Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
        //
        final String responseJson = "[ 1, 2, 3]";
        final InputStream byteArrayInputStream = new ByteArrayInputStream(responseJson.getBytes(StandardCharsets.UTF_8));
        Mockito.when(httpEntity.getContent()).thenReturn(byteArrayInputStream);
        //
        final List<Integer> list = listResponseHandler.handleResponse(httpResponse);
        Assert.assertNotNull(list);
        Assert.assertEquals(3L, list.size());
        Assert.assertEquals(1, list.get(0).longValue());
        Assert.assertEquals(2, list.get(1).longValue());
        Assert.assertEquals(3, list.get(2).longValue());
    }
}