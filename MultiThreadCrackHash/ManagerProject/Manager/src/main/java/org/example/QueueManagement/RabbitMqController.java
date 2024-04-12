package org.example.QueueManagement;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.example.Controller.DbService;
import org.example.Model.Task;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

@Component
@Scope("singleton")
public class RabbitMqController {
    /*
    * Здесь будет конвертирование в xml и отправка таски в очередь
    * */
    private final String topicExchangeName = "MANAGER_EXCHANGE";
    private final String Manager2WorkerKey = "Manager2WorkerKey";

    private final RabbitTemplate rabbitTemplate;
    private final DbService dbService;

    public RabbitMqController(RabbitTemplate rabbitTemplate, DbService cont) {
        this.rabbitTemplate = rabbitTemplate;
        this.dbService = cont;
    }

    public void queueTask(Task task){
        System.out.println("Start: RabbitMqController");
        StringWriter stringWriter = new StringWriter();

        try{
            JAXBContext buff = JAXBContext.newInstance(Task.class);
            Marshaller marshaller1 = buff.createMarshaller();
            marshaller1.marshal(task, stringWriter);
            System.out.println(stringWriter);

            System.out.println("Sending from manger to queue");
            rabbitTemplate.convertAndSend(topicExchangeName, Manager2WorkerKey, stringWriter.toString());
            System.out.println("End: RabbitMqController");
        }
        catch (JAXBException e){
            e.printStackTrace();
        }
    }

    public void queueTasks(List<Task> tasks){
        for (Task task: tasks){
            queueTask(task);
        }
    }

    @RabbitListener(queues = "Worker2ManagerQueue")
    public void getTaskFromWorker2ManagerQueue(@RequestBody String xmlTask){
        System.out.println("Message receiving is started");
        Task buff = null;
        try{
            JAXBContext jaxbContext = JAXBContext.newInstance(Task.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            buff = (Task) unmarshaller.unmarshal(new StringReader(xmlTask));
            dbService.saveTaskIntoDB(buff);
        }
        catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        System.out.println("\uD83D\uDE04 Message receiving is ended");
    }
}
