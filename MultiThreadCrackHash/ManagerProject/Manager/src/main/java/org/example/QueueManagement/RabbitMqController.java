package org.example.QueueManagement;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.example.Model.Task;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Scope("singleton")
public class RabbitMqController {
    /*
    * Здесь будет конвертирование в xml и отправка таски в очередь
    * */

    private final String topicExchangeName = "MANAGER_EXCHANGE";
    private final String Manager2WorkerKey = "Manager2WorkerKey";

    private final RabbitTemplate rabbitTemplate;
    private final CopyOnWriteArrayList<Task> collection;

    public RabbitMqController(RabbitTemplate rabbitTemplate, CopyOnWriteArrayList<Task> collection) {
        this.rabbitTemplate = rabbitTemplate;
        this.collection = collection;
    }

    public void sendTaskToQueue(Task task){
        StringWriter stringWriter = new StringWriter();

        try{
            JAXBContext buff = JAXBContext.newInstance(Task.class);
            Marshaller marshaller1 = buff.createMarshaller();
            marshaller1.marshal(task, stringWriter);
            System.out.println(stringWriter);
        }
        catch (JAXBException e){
            e.printStackTrace();
        }

        rabbitTemplate.convertAndSend(topicExchangeName, Manager2WorkerKey, stringWriter.toString());
    }

    @RabbitListener(queues = "Worker2ManagerQueue")
    public void getTaskFromWorker2ManagerQueue(@RequestBody String xmlTask){
        Task buff = null;

        try{
            JAXBContext jaxbContext = JAXBContext.newInstance(Task.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            buff = (Task) unmarshaller.unmarshal(new StringReader(xmlTask));
            System.out.println(buff.word);
        }
        catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        // TODO: здесь реализовать добавление выполненных тасок в БД.

    }
}
