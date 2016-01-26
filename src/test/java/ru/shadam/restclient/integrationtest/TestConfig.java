package ru.shadam.restclient.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import ru.shadam.restclient.spring.EnableRestClient;

/**
 * @author sala
 */
@EnableRestClient
public class TestConfig {
    @Bean
    public ObjectMapper objectMapper() { return new ObjectMapper(); }

    @Bean
    public HttpClient httpClient() { return HttpClientBuilder.create().build(); }
}
