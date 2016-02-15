package ru.shadam.ferry.simple.responsehandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import ru.shadam.ferry.analyze.MethodContext;
import ru.shadam.ferry.factory.response.ResponseWrapper;
import ru.shadam.ferry.factory.result.ResultExtractor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
        final ResponseWrapper response = getResponseWrapper("[\"abc\", \"def\"]");
        final List list = responseHandler.extractResponse(response);
        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("abc", list.get(0));
        Assert.assertEquals("def", list.get(1));
    }

    @Test
    public void handleVoid() throws Exception {
        final MethodContext methodContextWithReturnType = getMethodContextWithReturnType(Void.TYPE);
        Assert.assertFalse(new ObjectMapperResponseHandlerFactory(new ObjectMapper()).canCreateExtractor(methodContextWithReturnType));
    }


    @Test
    public void testString() throws Exception {
        final MethodContext methodContext = getMethodContextWithReturnType(String.class);
        Assert.assertTrue(new ObjectMapperResponseHandlerFactory(new ObjectMapper()).canCreateExtractor(methodContext));
    }

    @Test
    public void testString2() throws Exception {
        final MethodContext methodContext = getMethodContextWithReturnType(String.class);
        final ObjectMapperResponseHandlerFactory objectMapperResponseHandlerFactory = new ObjectMapperResponseHandlerFactory(new ObjectMapper());
        final ResultExtractor<Object> resultExtractor = objectMapperResponseHandlerFactory.getResultExtractor(methodContext);
        final String str = "\"abc\"";
        final ResponseWrapper responseWrapper = getResponseWrapper(str);
        final Object res = resultExtractor.extractResponse(responseWrapper);
        Assert.assertEquals("abc", res);
    }

    private ResponseWrapper getResponseWrapper(String str) {
        final ResponseWrapper responseWrapper = Mockito.mock(ResponseWrapper.class);
        Mockito.when(responseWrapper.getInputStream()).thenReturn(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)));
        return responseWrapper;
    }

    private MethodContext getMethodContextWithReturnType(Class<?> clazz) {
        final MethodContext methodContext = Mockito.mock(MethodContext.class);
        Mockito.when(methodContext.returnType()).thenReturn(clazz);
        return methodContext;
    }

    @Test
    public void testRawInt() throws IOException {
        final MethodContext methodContextWithReturnType = getMethodContextWithReturnType(int.class);
        final ObjectMapperResponseHandlerFactory factory = new ObjectMapperResponseHandlerFactory(new ObjectMapper());
        Assert.assertTrue(factory.canCreateExtractor(methodContextWithReturnType));
        final Object res = factory.getResultExtractor(methodContextWithReturnType).extractResponse(getResponseWrapper("123"));
        Assert.assertEquals(123, res);

    }
}