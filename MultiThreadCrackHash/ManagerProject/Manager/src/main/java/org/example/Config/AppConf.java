package org.example.Config;

import lombok.SneakyThrows;

import org.example.Model.Task;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.RoutingConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;

import java.math.RoundingMode;
import java.util.concurrent.CopyOnWriteArrayList;


@Configuration
public class AppConf {

    static final String queueName = "ManagerQueue";
    static final String exchangeName = "ManagerExchange";
    static final String routingKey = "RoutingKey";

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

    @Bean
    Queue queue() {
        // скорее всего, здесь нужно будет поменять durable
        return new Queue(queueName, false);
    }

    @Bean
    DirectExchange exchange(){
        return new DirectExchange(exchangeName);
    }

    @Bean
    Binding binding(Queue queue, DirectExchange directExchange){
        return BindingBuilder.bind(queue).to(directExchange).with(routingKey);
    }

}
