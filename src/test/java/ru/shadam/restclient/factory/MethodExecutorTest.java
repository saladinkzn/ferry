package ru.shadam.restclient.factory;

import com.google.common.collect.ImmutableMap;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import ru.shadam.restclient.factory.executor.HttpClientMethodExecutor;

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
        final HttpUriRequest httpUriRequest = requestExecutor.getHttpUriRequest(ImmutableMap.of("param1", "value1", "param2", "value2"));
        Assert.assertEquals("http://example.com?param1=value1&param2=value2", httpUriRequest.getURI().toString());
        Assert.assertEquals("GET", httpUriRequest.getMethod());
    }
}
