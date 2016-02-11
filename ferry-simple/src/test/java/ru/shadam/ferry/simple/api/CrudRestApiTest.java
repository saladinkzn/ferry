package ru.shadam.ferry.simple.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.io.CharStreams;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.shadam.ferry.annotations.Url;
import ru.shadam.ferry.api.CrudRestApi;
import ru.shadam.ferry.simple.DefaultClientImplementationFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author sala
 */
public class CrudRestApiTest {
    private HttpClient httpClient;
    private TestCrudRestApi testCrudRestApi;

    @Before
    public void setUp() {
        httpClient = Mockito.mock(HttpClient.class);
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        final DefaultClientImplementationFactory defaultClientImplementationFactory = new DefaultClientImplementationFactory(httpClient, objectMapper);
        testCrudRestApi = defaultClientImplementationFactory.getInterfaceImplementation(TestCrudRestApi.class);
    }

    @Test
    public void testGetAll() throws IOException {
        final List<Entity> all = testCrudRestApi.getAll();
        //
        final HttpUriRequest value = captureHttpUriRequest();
        Assert.assertEquals("GET", value.getMethod());
        Assert.assertEquals("http://example.com/api/entities/", value.getURI().toString());
    }

    @Test
    public void testGetOne() throws IOException {
        final Entity entity = testCrudRestApi.getById(1L);
        //
        final HttpUriRequest value = captureHttpUriRequest();
        Assert.assertEquals("GET", value.getMethod());
        Assert.assertEquals("http://example.com/api/entities/1", value.getURI().toString());
    }

    @Test
    public void testCreate() throws IOException {
        final Entity entity = new Entity(123);
        final Entity updatedEntity = testCrudRestApi.create(entity);
        //
        final HttpUriRequest httpUriRequest = captureHttpUriRequest();
        Assert.assertEquals("PUT", httpUriRequest.getMethod());
        Assert.assertEquals("http://example.com/api/entities/", httpUriRequest.getURI().toString());
        final HttpEntityEnclosingRequestBase entityEnclosingRequestBase = (HttpEntityEnclosingRequestBase) httpUriRequest;
        final String entityString = CharStreams.toString(new InputStreamReader(entityEnclosingRequestBase.getEntity().getContent()));
        Assert.assertEquals("{\"id\":123}", entityString);
    }

    @Test
    public void testUpdate() throws IOException {
        final Entity entity = new Entity(123);
        final Entity updatedEntity = testCrudRestApi.update(1L, entity);
        //
        final HttpUriRequest httpUriRequest = captureHttpUriRequest();
        Assert.assertEquals("POST", httpUriRequest.getMethod());
        Assert.assertEquals("http://example.com/api/entities/1", httpUriRequest.getURI().toString());
        final HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase = (HttpEntityEnclosingRequestBase) httpUriRequest;
        final String entityString = CharStreams.toString(new InputStreamReader(httpEntityEnclosingRequestBase.getEntity().getContent()));
        Assert.assertEquals("{\"id\":123}", entityString);
    }

    @Test
    public void testDelete() throws IOException {
        testCrudRestApi.deleteById(1L);
        //
        final HttpUriRequest httpUriRequest = captureHttpUriRequest();
        Assert.assertEquals("DELETE", httpUriRequest.getMethod());
        Assert.assertEquals("http://example.com/api/entities/1", httpUriRequest.getURI().toString());
    }

    HttpUriRequest captureHttpUriRequest() throws IOException {
        final ArgumentCaptor<HttpUriRequest> httpUriRequestArgumentCaptor = ArgumentCaptor.forClass(HttpUriRequest.class);
        Mockito.verify(httpClient, Mockito.only()).execute(httpUriRequestArgumentCaptor.capture());
        return httpUriRequestArgumentCaptor.getValue();
    }

    private static class Entity {
        private long id;

        public Entity(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }
    }

    @Url("http://example.com/api/entities")
    interface TestCrudRestApi extends CrudRestApi<Entity, Long> { }
}
