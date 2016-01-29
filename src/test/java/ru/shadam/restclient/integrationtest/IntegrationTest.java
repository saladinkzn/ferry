package ru.shadam.restclient.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.shadam.restclient.annotations.Param;
import ru.shadam.restclient.annotations.Url;
import ru.shadam.restclient.factory.ClientImplFactory;
import ru.shadam.restclient.integrationtest.dto.Album;
import ru.shadam.restclient.integrationtest.dto.Photo;

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
        final PhotoRepository photoRepository = new ClientImplFactory(httpClient, objectMapper).getInterfaceImplementation(PhotoRepository.class);
        photoRepository.getPhotos(1L, 0L, null);
        Mockito.verify(httpClient).execute(httpUriRequestArgumentCaptor.capture(), Mockito.<ResponseHandler>any());
        final HttpUriRequest value = httpUriRequestArgumentCaptor.getValue();
        Assert.assertNotNull(photoRepository);
        //
        Assert.assertEquals("GET", value.getMethod());
        Assert.assertEquals("https://api.vk.com/methods/photos.get?photo_ids=null&owner_id=1&album_id=0", value.getURI().toString());
    }

    /**
     * @author sala
     */
    @Url("https://api.vk.com/methods/photos.")
    public interface PhotoRepository {
        @Url("get")
        public List<Photo> getPhotos(@Param("owner_id") Long ownerId,
                                     @Param("album_id") Long albumId,
                                     @Param("photo_ids") String photoIds);

        @Url("getAlbums")
        public List<Album> getAlbums(@Param("owner_id") Long ownerId,
                                     @Param("ablum_ids") String albumIds,
                                     int offset,
                                     int count);
    }
}
