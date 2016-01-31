package ru.shadam.restclient.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.shadam.restclient.factory.ClientImplFactory;

import java.io.IOException;

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
        final PhotoRepository photoRepository = new ClientImplFactory(httpClient, objectMapper).getInterfaceImplementation(PhotoRepository.class);
        photoRepository.getPhotos(1L, 0L, null);
        Mockito.verify(httpClient).execute(httpUriRequestArgumentCaptor.capture(), Mockito.<ResponseHandler>any());
        final HttpUriRequest value = httpUriRequestArgumentCaptor.getValue();
        Assert.assertNotNull(photoRepository);
        //
        Assert.assertEquals("GET", value.getMethod());
        Assert.assertEquals("https://api.vk.com/methods/photos.get?v=5.41&owner_id=1&album_id=0&photo_ids=null", value.getURI().toString());
    }

}
