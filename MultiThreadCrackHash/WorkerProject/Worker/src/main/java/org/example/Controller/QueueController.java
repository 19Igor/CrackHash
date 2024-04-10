package org.example.Controller;

import lombok.AllArgsConstructor;
import org.example.Handlers.ObjectiveHandler;
import org.example.Model.Task;
import org.example.QueueManagement.MyMarshaller;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("queue")
@AllArgsConstructor
public class QueueController {

    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final static String serviceURL = "http://manager-service:8080/queue";
    private final static String localURL = "http://localhost:8080/queue";
    private final String WORKER_EXCHANGE = "WORKER_EXCHANGE";
    private final String Worker2ManagerKey = "Worker2ManagerKey";
    private final MyMarshaller marshaller;

    @RabbitListener(queues = "Manager2WorkerQueue")
//    @Async
    public void sendDoneTask(@RequestBody String xmlTask){
        System.out.println("------------------------------------------Line------------------------------------------");
        System.out.println(" It's an xmlTask: " + xmlTask);

        Task buff = null;
        buff = marshaller.convertXmlString2Task(xmlTask); // убрать static у convertXmlString2Task
        System.out.println("First check of buff.maxLength: " + buff.length);

        System.out.println("Search of the word is started");
        System.out.println("hash is " + buff.hash);
        System.out.println("maxLength is " + buff.length); // без понятия, почему программа выводит длину = 0
        System.out.println("first word is " + buff.firstWord);
        System.out.println("last word is " + buff.lastWord);
        buff.word = ObjectiveHandler.convertHash2Word(buff.hash, buff.length, buff.firstWord, buff.lastWord);
        System.out.println("\uD83E\uDD70  The word is " + buff.word);
        System.out.println("Search of the word is ended");


//        String stringBuff = marshaller.convertTask2XmlString(buff);
//        System.out.println("Return transmission is started");
//        rabbitTemplate.convertAndSend(WORKER_EXCHANGE, Worker2ManagerKey, stringBuff);

    }

    public static void main(String[] args) {
        Task buff = new Task();
        buff.hash = "e2fc714c4727ee9395f324cd2e7f331f";
        buff.length = 4;
        buff.firstWord = 'a';
        buff.lastWord = 'r';

        buff.word = ObjectiveHandler.convertHash2Word(buff.hash, buff.length, buff.firstWord, buff.lastWord);
        System.out.println("Word is " + buff.word);

    }

}
