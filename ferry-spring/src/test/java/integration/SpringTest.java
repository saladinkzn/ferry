package integration;

import integration.ferries.Ferry1;
import integration.ferries.Ferry2;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import ru.shadam.ferry.spring.config.EnableFerries;

/**
 * @author sala
 */
public class SpringTest {
    @EnableFerries(value = "integration.ferries")
    @Configuration
    public static class TestConfiguration {
    }

    @Test
    public void test1() {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfiguration.class);
        final Ferry1 ferry1 = context.getBean(Ferry1.class);
        Assert.assertNotNull(ferry1);
        final Ferry2 ferry2 = context.getBean(Ferry2.class);
        Assert.assertNotNull(ferry2);
    }

}
