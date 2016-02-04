package ru.shadam.restclient.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import ru.shadam.restclient.factory.responsehandler.ObjectMapperResponseHandler;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author sala
 */
public class ObjectMapperResponseHandlerTest {

    @Test
    public void handleResponse() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();
        final ObjectMapperResponseHandler<List> responseHandler = new ObjectMapperResponseHandler<>(objectMapper, List.class);
        final HttpResponse response = Mockito.mock(HttpResponse.class);
        final HttpEntity httpEntity = Mockito.mock(HttpEntity.class);
        Mockito.when(response.getEntity()).thenReturn(httpEntity);
        Mockito.when(httpEntity.getContent()).thenReturn(new ByteArrayInputStream("[\"abc\", \"def\"]".getBytes(StandardCharsets.UTF_8)));
        final List list = responseHandler.handleResponse(response);
        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("abc", list.get(0));
        Assert.assertEquals("def", list.get(1));
    }
}