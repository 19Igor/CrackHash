package org.example.Config;

import lombok.SneakyThrows;
import org.example.Model.Task;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.CopyOnWriteArrayList;


@Configuration
public class AppConf{

    static final String Manager2WorkerQueue = "Manager2WorkerQueue";
    static final String Worker2ManagerQueue = "Worker2ManagerQueue";
    static final String MANAGER_EXCHANGE = "MANAGER_EXCHANGE";
    static final String WORKER_EXCHANGE = "WORKER_EXCHANGE";
    static final String Manager2WorkerKey = "Manager2WorkerKey";
    static final String Worker2ManagerKey = "Worker2ManagerKey";

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
    Queue getManager2WorkerQueue() {
        // скорее всего, здесь нужно будет поменять durable
        return new Queue(Manager2WorkerQueue, true);
    }
//----------------------------------------------------------------------------------------------------------------------
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory("rabbitmq");
        cachingConnectionFactory.setUsername("user");
        cachingConnectionFactory.setPassword("user");
        return cachingConnectionFactory;
    }
    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }
//----------------------------------------------------------------------------------------------------------------------
    @Bean
    DirectExchange managerExchange(){
        return new DirectExchange(MANAGER_EXCHANGE);
    }

    @Bean
    Binding binding(Queue getManager2WorkerQueue, DirectExchange managerExchange){
        return BindingBuilder.bind(getManager2WorkerQueue).to(managerExchange).with(Manager2WorkerKey);
    }

    @Bean
    Queue getWorker2ManagerQueue(){
        return new Queue(Worker2ManagerQueue, true);
    }
    
    @Bean
    DirectExchange workerExchange(){
        return new DirectExchange(WORKER_EXCHANGE);
    }

    @Bean
    Binding binding1(Queue getWorker2ManagerQueue, DirectExchange workerExchange){
        return BindingBuilder.bind(getWorker2ManagerQueue).to(workerExchange).with(Worker2ManagerKey);
    }
}
