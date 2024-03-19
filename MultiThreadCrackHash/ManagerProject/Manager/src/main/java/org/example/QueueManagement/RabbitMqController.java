package org.example.QueueManagement;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.example.Model.Task;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.StringWriter;

@Component
@Scope("singleton")
public class RabbitMqController {
    /*
    * Здесь будет конвертирование в xml и отправка таски в очередь
    * */

    private final String topicExchangeName = "ManagerExchange";
    private final String routingKey = "RoutingKey";
    private final RabbitTemplate rabbitTemplate;

    public RabbitMqController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
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

        rabbitTemplate.convertAndSend(topicExchangeName, routingKey, stringWriter.toString());
    }
}
