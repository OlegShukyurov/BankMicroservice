package com.shukyurov.BankMicroservice.config;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public OkHttpClient client() {
        return new OkHttpClient();
    }

    @Bean
    public Feign.Builder feignBuilder() {
        return Feign.builder()
                .client(client())
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder());
    }

}
