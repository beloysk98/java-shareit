package ru.practicum.shareit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BookingClient;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.client.RequestClient;
import ru.practicum.shareit.client.UserClient;

@Configuration
public class WebClientConfig {

    @Bean
    public BookingClient bookingClient(RestTemplateBuilder builder,
                                       @Value("${shareit-server.url}") String serverUrl) {
        return new BookingClient(createRestTemplate(builder, serverUrl));
    }

    @Bean
    public ItemClient itemClient(RestTemplateBuilder builder,
                                 @Value("${shareit-server.url}") String serverUrl) {
        return new ItemClient(createRestTemplate(builder, serverUrl));
    }

    @Bean
    public RequestClient requestClient(RestTemplateBuilder builder,
                                       @Value("${shareit-server.url}") String serverUrl) {
        return new RequestClient(createRestTemplate(builder, serverUrl));
    }

    @Bean
    public UserClient userClient(RestTemplateBuilder builder,
                                 @Value("${shareit-server.url}") String serverUrl) {
        return new UserClient(createRestTemplate(builder, serverUrl));
    }

    private RestTemplate createRestTemplate(RestTemplateBuilder builder, String serverUrl) {
        return builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .build();
    }
}