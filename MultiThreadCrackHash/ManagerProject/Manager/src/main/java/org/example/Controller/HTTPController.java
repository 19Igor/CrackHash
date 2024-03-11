package org.example.Controller;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.example.Const.WorkerStatus;
import org.example.Model.*;
import org.example.QueueManagement.RabbitMqController;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("manager")
@AllArgsConstructor
@EnableAsync
@EnableScheduling
public class HTTPController {

    private final RestTemplate restTemplate;
    private CopyOnWriteArrayList<Task> collection;
    private final String RESERVED_ID = "730a04e6-4de9-41f9-9d5b-53b88b17afac";
    private static int currentTaskCounter = 0;
    /*
    * получается, что spring boot сам внедрит singleton объекта mqController.
    */


    private final RabbitMqController mqController;

    @PostMapping()
    public RequestedID getUserRequest(@RequestBody RequestDto requestDto) {

        int objectiveID = addTask2Collection(createWorkerTask(requestDto));

        // здесь нужно конвертировать таску в xml и отправить в очередь
        {
            Task task = new Task();
            task.word = "abu";
            task.status = WorkerStatus.IN_PROGRESS;
            task.hash = "gazali";
            mqController.sendTaskToQueue(task);
        }




        invokeWorker(new HttpEntity<>(Objects.requireNonNull(getObjective(objectiveID))));

        return new RequestedID(RESERVED_ID);
    }


    @GetMapping
    public  Response2User sendResult2User(@RequestParam("id") String id) {

        for (Task task : collection) {
            // при нескольких тасках прога выводит первую таску из очереди
            // это из-за того, что я привязал к task.userID
            if (task.userID.equals(id)) {
                double workingTimeSec = (System.currentTimeMillis() - task.creationTime) / 1000.0;
                int TIME_LIMIT = 15;
                if (task.status.equals(WorkerStatus.IN_PROGRESS) && workingTimeSec >= TIME_LIMIT){
                    // здесь рассматривается случай, когда время таски превышено
                    // здесь ещё нужно отправить worker, чтобы он прекратил выполнение текущей задачи
                    // и при том НЕ завершился
                    return new Response2User(WorkerStatus.ERROR, null);
                }
                else if (task.status.equals(WorkerStatus.IN_PROGRESS) && task.word == null){
                    // здесь рассматривается случай, когда таска выполняется от 0 и до 15 сек.
                    return new Response2User(WorkerStatus.IN_PROGRESS, null);
                }
                // здесь нужно очистить нашу коллекцию тасак
                deleteAllElementsFromTaskQueue(id);
                return new Response2User(WorkerStatus.READY, task.word);
            }
        }
        return null;
    }

    private void deleteAllElementsFromTaskQueue(String userID){
        collection.removeIf(buff -> buff.userID.equals(userID));
    }

    private void invokeWorker(HttpEntity<Task> requestDtoHttpEntity){
        String localURL = "http://localhost:8081/queue";
        String serviceURL = "http://worker-service:8081/queue";

        CompletableFuture.runAsync(() -> {
            /*
             *  Любые запросы должны стараться завершиться как можно быстрее.
             */
            try {
                restTemplate.postForEntity(localURL, requestDtoHttpEntity, String.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private Task createWorkerTask(RequestDto requestBody){
        Task newTask = new Task();
        newTask.userID = RESERVED_ID;
        newTask.taskID = currentTaskCounter++;
        newTask.status = WorkerStatus.IN_PROGRESS;
        newTask.hash = requestBody.getHash();
        newTask.maxLength = requestBody.getMaxLength();
        newTask.creationTime = System.currentTimeMillis();
        return newTask;
    }

    private int addTask2Collection(Task task){
        collection.add(task);
        return task.taskID;
    }

    private Task getObjective(int id){
        for (Task task : collection) {
            if (task.taskID == id) {
                return task;
            }
        }
        return null;
    }

    private void test1(){
        Task task = new Task();
        task.word = "abu";
        task.status = WorkerStatus.IN_PROGRESS;
        task.hash = "gazali";
        convertTask2XML(task);
    }
    private void convertTask2XML(Task task){
//        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("MarshallerBean.xml");
        ApplicationContext context = new ClassPathXmlApplicationContext("MarshallerBean.xml");

        Jaxb2Marshaller marshaller = (Jaxb2Marshaller) context.getBean("jaxb2Marshaller");

        StringWriter writer = new StringWriter();
        marshaller.marshal(task, new StreamResult(writer));

        String s = writer.toString();
        System.out.println("It's the s:");
        System.out.println();

    }

}
