package integration;

import org.junit.Test;
import ru.shadam.ferry.annotations.Param;
import ru.shadam.ferry.annotations.Url;
import ru.shadam.ferry.core.CoreClientImplementationFactory;

/**
 * @author timur.shakurov@dz.ru
 */
public class TestVK {
    public interface SimpleAPI {
        @Url("https://api.vk.com/method/users.get")
        String getContent(@Param("user_ids") String userIds);
    }

    @Test
    public void testVK() {
        final CoreClientImplementationFactory coreClientImplementationFactory = new CoreClientImplementationFactory();
        final SimpleAPI interfaceImplementation = coreClientImplementationFactory.getInterfaceImplementation(SimpleAPI.class);
        final String result = interfaceImplementation.getContent("tshakurov");
        System.out.println(result);
    }
}
