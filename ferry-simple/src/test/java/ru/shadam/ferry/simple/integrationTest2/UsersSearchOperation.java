package ru.shadam.ferry.simple.integrationTest2;

import ru.shadam.ferry.annotations.Param;
import ru.shadam.ferry.annotations.Url;

import java.util.Map;

/**
 * @author sala
 */
public interface UsersSearchOperation {
    @Url("https://api.vk.com/method/users.search")
    public Map<String, Object>  execute(@Param("q") String q, @Param Map<String, Object> params);

    @Url("https://api.vk.com/method/users.search")
    Map<String, Object> execute(@Param("q") String q, @Param Pageable pageable);
}
