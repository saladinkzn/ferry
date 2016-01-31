package ru.shadam.restclient.factory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.omg.CORBA.Object;

import java.util.Map;

/**
 * @author sala
 */
public class MethodExecutorTest {
    @Test
    public void testBuildHttpRequest() {
        HttpClient client = Mockito.mock(HttpClient.class);
        final Map<String, String> constImplicitParameters = Maps.newHashMap();
        final MethodExecutor<Object> requestExecutor = new MethodExecutor<>(
                client,
                "GET",
                Sets.newHashSet("param1", "param2"),
                "http://example.com",
                Mockito.mock(ResponseHandler.class),
                ImmutableMap.of(0, "param1", 1, "param2"), constImplicitParameters);
        final HttpUriRequest httpUriRequest = requestExecutor.getHttpUriRequest(ImmutableMap.of("param1", "value1", "param2", "value2"));
        Assert.assertEquals("http://example.com?param1=value1&param2=value2", httpUriRequest.getURI().toString());
        Assert.assertEquals("GET", httpUriRequest.getMethod());
    }
}
