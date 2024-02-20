package org.example.Controller;

import lombok.AllArgsConstructor;
import org.example.Handlers.ObjectiveHandler;
import org.example.Model.Task;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("queue")
@AllArgsConstructor
public class QueueController {

    private final RestTemplate restTemplate;
    private final static String serviceURL = "http://manager-service:8080/queue";
    private final static String localURL = "http://localhost:8080/queue";

    @PostMapping
    // этот метод вызывается внутри бизнес логики воркера
    // этот метод должен отправить менеджеру готовую таску
    // после чего, менеджер положит её в коллекцию
    public void sendDoneTask(@RequestBody Task task){
        task.word = ObjectiveHandler.convertHash2Word(task.hash, task.maxLength);
        HttpEntity<Task> requestTaskHttpEntity = new HttpEntity<>(task);
        restTemplate.postForEntity(serviceURL, requestTaskHttpEntity, Void.class);
    }

}