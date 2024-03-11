package org.example.QueueManagement;

import org.example.Const.WorkerStatus;
import org.example.Model.Task;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class RabbitMqController {
    /*
    * Здесь будет конвертирование в xml и отправка таски в очередь
    * */

    private final RabbitTemplate rabbitTemplate;
    private final MyMarshaller marshaller;

    public RabbitMqController(RabbitTemplate rabbitTemplate, MyMarshaller marshaller) {
        this.rabbitTemplate = rabbitTemplate;
        this.marshaller = marshaller;
    }

    public void sendTaskToQueue(Task task){
        // сначала таску нужно конвертировать в xml и потом в String
        String xmlTask = marshaller.convertTask2XmlString(task);



    }
}
