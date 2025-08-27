package com.innowise.OrderService.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {

        return builder.baseUrl("http://localhost:8081").build(); // 8081 if docker
    }
}
