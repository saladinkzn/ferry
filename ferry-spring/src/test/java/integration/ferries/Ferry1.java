package integration.ferries;

import ru.shadam.ferry.annotations.Url;

import java.util.List;

/**
 * @author sala
 */
public interface Ferry1 {
    @Url("http://example.com")
    public List<String> getResult();
}
