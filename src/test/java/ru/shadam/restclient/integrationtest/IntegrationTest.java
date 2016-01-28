package ru.shadam.restclient.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Assert;
import org.junit.Test;
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
        final ObjectMapper objectMapper = new ObjectMapper();
        final PhotoRepository photoRepository = new ClientImplFactory(httpClient, objectMapper).getInterfaceImplementation(PhotoRepository.class);
        Assert.assertNotNull(photoRepository);
        photoRepository.getPhotos(1L, 0L, null);
        Mockito.verify(httpClient).execute(Mockito.<HttpUriRequest>any(), Mockito.<ResponseHandler>any());
    }
}
