package ru.shadam.ferry.factory;

import com.google.common.collect.ImmutableMap;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import ru.shadam.ferry.factory.executor.HttpClientMethodExecutor;

/**
 * @author sala
 */
public class MethodExecutorTest {
    @Test
    public void testBuildHttpRequest() {
        HttpClient client = Mockito.mock(HttpClient.class);
        final HttpClientMethodExecutor<Object> requestExecutor = new HttpClientMethodExecutor<>(
                client,
                "GET",
                "http://example.com",
                Mockito.mock(ResponseHandler.class));
        final HttpUriRequest httpUriRequest = requestExecutor.getHttpUriRequest(ImmutableMap.of("param1", "value1", "param2", "value2"), ImmutableMap.<String, Object>of());
        Assert.assertEquals("http://example.com?param1=value1&param2=value2", httpUriRequest.getURI().toString());
        Assert.assertEquals("GET", httpUriRequest.getMethod());
    }

    @Test
    public void testBuildHttpRequest2() {
        HttpClient client = Mockito.mock(HttpClient.class);
        final HttpClientMethodExecutor<Object> requestExecutor = new HttpClientMethodExecutor<>(
                client,
                "GET",
                "http://example.com/:id",
                Mockito.mock(ResponseHandler.class));
        final HttpUriRequest httpUriRequest = requestExecutor.getHttpUriRequest(ImmutableMap.<String, Object>of(), ImmutableMap.<String, Object>of("id", 1));
        Assert.assertEquals("http://example.com/1", httpUriRequest.getURI().toString());
        Assert.assertEquals("GET", httpUriRequest.getMethod());
    }
}
