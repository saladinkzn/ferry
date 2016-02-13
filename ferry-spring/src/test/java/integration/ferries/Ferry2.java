package integration.ferries;

import ru.shadam.ferry.annotations.Url;

/**
 * @author sala
 */
public interface Ferry2 {
    @Url("http://example.com")
    String getResult();
}
