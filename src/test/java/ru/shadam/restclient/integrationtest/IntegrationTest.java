package ru.shadam.restclient.integrationtest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author sala
 */
public class IntegrationTest {

    private ApplicationContext applicationContext;

    @Before
    public void prepare() {
        applicationContext = new AnnotationConfigApplicationContext(TestConfig.class);
    }

    @Test
    public void testPhotoRepository() {
        final PhotoRepository photoRepository = applicationContext.getBean(PhotoRepository.class);
        photoRepository.getPhotos(1L, 0L, null);
    }
}
