package ru.shadam.ferry.simple.executor;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStreamReader;

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

    @Test
    public void testBuildHttpRequest3() throws IOException {
        HttpClient client = Mockito.mock(HttpClient.class);
        final HttpClientMethodExecutor methodExecutor = new HttpClientMethodExecutor<>(
                client,
                "POST",
                "http://example.com/:id",
                Mockito.mock(ResponseHandler.class)
        );
        final String requestBody = "{\"id\": 1, \"name\": \"lol\"}";
        final HttpUriRequest httpUriRequest = methodExecutor.getHttpUriRequest(
                ImmutableMap.<String, Object>of(),
                ImmutableMap.<String, Object>of("id", 1),
                requestBody
        );
        final HttpEntityEnclosingRequestBase entityEnclosingRequestBase = (HttpEntityEnclosingRequestBase) httpUriRequest;
        Assert.assertEquals("POST", httpUriRequest.getMethod());
        Assert.assertEquals("http://example.com/1", httpUriRequest.getURI().toString());
        final HttpEntity entity = entityEnclosingRequestBase.getEntity();
        final String entityContent = CharStreams.toString(new InputStreamReader(entity.getContent()));
        Assert.assertEquals(requestBody, entityContent);
    }
}
