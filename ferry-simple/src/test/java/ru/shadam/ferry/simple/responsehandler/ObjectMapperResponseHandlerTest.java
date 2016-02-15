package ru.shadam.ferry.simple.responsehandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import ru.shadam.ferry.factory.response.ResponseWrapper;

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
        final ResponseWrapper response = Mockito.mock(ResponseWrapper.class);
        Mockito.when(response.getInputStream()).thenReturn(new ByteArrayInputStream("[\"abc\", \"def\"]".getBytes(StandardCharsets.UTF_8)));
        final List list = responseHandler.extractResponse(response);
        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("abc", list.get(0));
        Assert.assertEquals("def", list.get(1));
    }

//
//    @Test
//    public void testString() throws Exception {
//        final ObjectMapper objectMapper = new ObjectMapper();
//        final ObjectMapperResponseHandler<String> handler = new ObjectMapperResponseHandler<>(objectMapper, String.class);
//        final ResponseWrapper responseWrapper = Mockito.mock(ResponseWrapper.class);
//        Mockito.when(responseWrapper.getInputStream()).thenReturn(new ByteArrayInputStream("{\"1\":123}".getBytes(StandardCharsets.UTF_8)));
//        final String result = handler.extractResponse(responseWrapper);
//        Assert.assertEquals("{\"1\":123}", result);
//    }
}