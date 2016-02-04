package ru.shadam.restclient.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.shadam.restclient.factory.ClientImplementationFactory;
import ru.shadam.restclient.factory.DefaultClientImplementationFactory;
import ru.shadam.restclient.implicit.ImplicitParameterProvider;
import ru.shadam.restclient.integrationtest.dto.Album;

import java.io.IOException;
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
        Mockito.verify(httpClient).execute(httpUriRequestArgumentCaptor.capture(), Mockito.<ResponseHandler>any());
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
        Mockito.verify(httpClient).execute(httpUriRequestArgumentCaptor.capture(), Mockito.<ResponseHandler>any());
        final HttpUriRequest value = httpUriRequestArgumentCaptor.getValue();
        //
        Assert.assertEquals("GET", value.getMethod());
        Assert.assertEquals("https://api.vk.com/methods/photos.getAlbums?access_token=ACCESS_TOKEN&owner_id=123&album_ids=456&offset=100&count=200", value.getURI().toString());
    }

}
