package com.deuceng.moviedive.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpConfig {//TMBD'e get isteği atmak için

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
