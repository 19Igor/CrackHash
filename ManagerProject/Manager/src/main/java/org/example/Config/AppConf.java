package org.example.Config;

import lombok.SneakyThrows;

import org.example.Model.Task;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CopyOnWriteArrayList;

@Configuration
public class AppConf {

    @SneakyThrows
    @Bean
    @Scope("singleton")
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    @Scope("singleton")
    public CopyOnWriteArrayList<Task> copyOnWriteArrayList(){
        return new CopyOnWriteArrayList<>();
    }
}
