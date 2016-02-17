package ru.shadam.ferry.simple.integrationTest2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import ru.shadam.ferry.factory.ClientImplementationFactory;
import ru.shadam.ferry.simple.DefaultClientImplementationFactory;

/**
 * @author sala
 */
public class IntTest2 {
    @Test
    public void testUsersSearch() {
        final ClientImplementationFactory clientImplementationFactory = new DefaultClientImplementationFactory(HttpClientBuilder.create().build(), new ObjectMapper());
        final UsersSearchOperation usersSearchOp = clientImplementationFactory.getInterfaceImplementation(UsersSearchOperation.class);
        System.out.println(usersSearchOp.execute("Дуров", ImmutableMap.of("sort", "name", "offset", 10, "count", 20)));
    }
}
