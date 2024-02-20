package org.example.Config;

import lombok.SneakyThrows;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Configuration
public class AppConf {

    @SneakyThrows
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
