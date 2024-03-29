package org.example.Controller;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.AllArgsConstructor;
import org.example.Handlers.ObjectiveHandler;
import org.example.Model.Task;
import org.example.QueueManagement.MyMarshaller;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.StringReader;

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
    @Async
    public void sendDoneTask(@RequestBody String xmlTask){

        Task buff = null;

//        try {
//            JAXBContext jaxbContext = JAXBContext.newInstance(Task.class);
//            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//            buff = (Task) unmarshaller.unmarshal(new StringReader(xmlTask));
//            System.out.println(buff.hash);
//        } catch (JAXBException e) {
//            throw new RuntimeException(e);
//        }

        buff = marshaller.convertXmlString2Task(xmlTask);

        buff.word = ObjectiveHandler.convertHash2Word(buff.hash, buff.maxLength);
        /*
        * На данный момент есть таска, в котором либо есть слово, либо нет.
        * После мы должны отправить таску в очередь, чтобы manager забрал её и положил себе в коллекцию.
        * */

        String stringBuff = marshaller.convertTask2XmlString(buff);
        rabbitTemplate.convertAndSend(WORKER_EXCHANGE, Worker2ManagerKey, stringBuff);



    }

}
