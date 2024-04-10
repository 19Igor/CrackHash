package org.example.Controller;

import lombok.AllArgsConstructor;
import org.example.Handlers.ObjectiveHandler;
import org.example.Model.Task;
import org.example.QueueManagement.MyMarshaller;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import static org.example.Const.Constants.WORKER_EXCHANGE;
import static org.example.Const.Constants.Worker2ManagerKey;


@RestController
@RequestMapping("queue")
@AllArgsConstructor
public class QueueController {

    private final RabbitTemplate rabbitTemplate;
    private final MyMarshaller marshaller;

    @RabbitListener(queues = "Manager2WorkerQueue")
    @Async
    public void sendDoneTask(@RequestBody String xmlTask){
        Task buff = null;
        buff = marshaller.convertXmlString2Task(xmlTask);
        buff.word = ObjectiveHandler.convertHash2Word(buff.hash, buff.length, buff.firstWord, buff.lastWord);
        String stringBuff = marshaller.convertTask2XmlString(buff);
        System.out.println("Return transmission is started.");
        rabbitTemplate.convertAndSend(WORKER_EXCHANGE, Worker2ManagerKey, stringBuff);
        System.out.println("Return transmission is ended.");
    }
}
