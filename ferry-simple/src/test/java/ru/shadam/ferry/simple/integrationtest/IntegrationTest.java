package ru.shadam.ferry.simple.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.CharStreams;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.shadam.ferry.factory.ClientImplementationFactory;
import ru.shadam.ferry.implicit.ImplicitParameterProvider;
import ru.shadam.ferry.implicit.ImplicitParameterWithNameProvider;
import ru.shadam.ferry.simple.DefaultClientImplementationFactory;
import ru.shadam.ferry.simple.integrationtest.dto.Album;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author sala
 */
public class IntegrationTest {

//    private ApplicationContext applicationContext;

//    @Before
//    public void prepare() {
//        applicationContext = new AnnotationConfigApplicationContext(TestConfig.class);
//    }

    @Test
    public void testPhotoRepository() throws IOException {
        final HttpClient httpClient = Mockito.mock(HttpClient.class);
        ArgumentCaptor<HttpUriRequest> httpUriRequestArgumentCaptor = ArgumentCaptor.forClass(HttpUriRequest.class);
        //
        //
        final ObjectMapper objectMapper = new ObjectMapper();
        final PhotoRepository photoRepository = new DefaultClientImplementationFactory(httpClient, objectMapper).getInterfaceImplementation(PhotoRepository.class);
        photoRepository.getPhotos(1L, 0L, null);
        Mockito.verify(httpClient).execute(httpUriRequestArgumentCaptor.capture());
        final HttpUriRequest value = httpUriRequestArgumentCaptor.getValue();
        Assert.assertNotNull(photoRepository);
        //
        Assert.assertEquals("GET", value.getMethod());
        Assert.assertEquals("https://api.vk.com/methods/photos.get?v=5.41&owner_id=1&album_id=0&photo_ids=null", value.getURI().toString());
    }

    @Test
    public void testPhotoRepository2() throws IOException {
        final HttpClient httpClient = Mockito.mock(HttpClient.class);
        final ArgumentCaptor<HttpUriRequest> httpUriRequestArgumentCaptor = ArgumentCaptor.forClass(HttpUriRequest.class);
        final ObjectMapper objectMapper = new ObjectMapper();
        //
        ImplicitParameterProvider accessTokenProvider = new ImplicitParameterProvider() {
            @Override
            public String provideValue() {
                return "ACCESS_TOKEN";
            }
        };
        //
        final ClientImplementationFactory clientImplementationFactory = new DefaultClientImplementationFactory(httpClient, objectMapper);
        clientImplementationFactory.registerImplicitParameterProvider("accessTokenProvider", accessTokenProvider);
        final PhotoRepository photoRepository = clientImplementationFactory.getInterfaceImplementation(PhotoRepository.class);
        //
        final List<Album> albums = photoRepository.getAlbums(123L, "456", 100, 200);
        Mockito.verify(httpClient).execute(httpUriRequestArgumentCaptor.capture());
        final HttpUriRequest value = httpUriRequestArgumentCaptor.getValue();
        //
        Assert.assertEquals("GET", value.getMethod());
        Assert.assertEquals("https://api.vk.com/methods/photos.getAlbums?access_token=ACCESS_TOKEN&owner_id=123&album_ids=456&offset=100&count=200", value.getURI().toString());
    }

    @Test
    public void testPhotoRepository3() throws IOException {
        final HttpClient httpClient = Mockito.mock(HttpClient.class);
        final ArgumentCaptor<HttpUriRequest> httpUriRequestArgumentCaptor = ArgumentCaptor.forClass(HttpUriRequest.class);
        final ObjectMapper objectMapper = new ObjectMapper();
        //
        ImplicitParameterProvider accessTokenProvider = new ImplicitParameterWithNameProvider() {
            @Override
            public String parameterName() {
                return "access_token";
            }

            @Override
            public String provideValue() {
                return "ACCESS_TOKEN";
            }
        };
        //
        final ClientImplementationFactory clientImplementationFactory = new DefaultClientImplementationFactory(httpClient, objectMapper);
        clientImplementationFactory.registerImplicitParameterProvider("accessTokenProvider", accessTokenProvider);
        final PhotoRepository photoRepository = clientImplementationFactory.getInterfaceImplementation(PhotoRepository.class);
        //
        final List<Album> albums = photoRepository.getAlbums2();
        Mockito.verify(httpClient).execute(httpUriRequestArgumentCaptor.capture());
        final HttpUriRequest value = httpUriRequestArgumentCaptor.getValue();
        //
        Assert.assertEquals("GET", value.getMethod());
        Assert.assertEquals("https://api.vk.com/methods/photos.getAlbums?access_token=ACCESS_TOKEN", value.getURI().toString());
    }

    @Test
    public void testPhotoRepository4() throws IOException {
        final HttpClient httpClient = Mockito.mock(HttpClient.class);
        final ArgumentCaptor<HttpUriRequest> httpUriRequestArgumentCaptor = ArgumentCaptor.forClass(HttpUriRequest.class);
        final ObjectMapper objectMapper = new ObjectMapper();
        final ClientImplementationFactory clientImplementationFactory = new DefaultClientImplementationFactory(httpClient, objectMapper);
        final PhotoRepository interfaceImplementation = clientImplementationFactory.getInterfaceImplementation(PhotoRepository.class);
        final String photoEntity = "{ name: \"My beach photo\" }";
        interfaceImplementation.uploadPhoto(photoEntity);
        Mockito.verify(httpClient, Mockito.only()).execute(httpUriRequestArgumentCaptor.capture());
        final HttpUriRequest httpUriRequest = httpUriRequestArgumentCaptor.getValue();
        Assert.assertEquals("POST", httpUriRequest.getMethod());
        Assert.assertEquals("https://api.vk.com/methods/photos.upload", httpUriRequest.getURI().toString());
        final HttpEntityEnclosingRequestBase entityEnclosingRequestBase = (HttpEntityEnclosingRequestBase) httpUriRequest;
        final HttpEntity httpEntity = entityEnclosingRequestBase.getEntity();
        Assert.assertEquals(photoEntity, CharStreams.toString(new InputStreamReader(httpEntity.getContent())));
    }

}
