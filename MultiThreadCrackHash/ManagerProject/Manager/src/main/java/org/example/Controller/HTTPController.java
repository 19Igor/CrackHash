package org.example.Controller;

import lombok.AllArgsConstructor;
import org.example.Const.WorkerStatus;
import org.example.DbManagement.TaskRepository;
import org.example.Model.*;
import org.example.QueueManagement.RabbitMqController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("manager")
@AllArgsConstructor
@EnableAsync
@EnableScheduling
public class HTTPController {

    private final RestTemplate restTemplate;
    private final CopyOnWriteArrayList<Task> collection;
    private final String RESERVED_ID = "730a04e6-4de9-41f9-9d5b-53b88b17afac";
    private static int currentTaskCounter = 0;
    private final RabbitMqController mqController;
    @Autowired
    private final TaskRepository taskRepository;


    @PostMapping()
    // точка старта
    public RequestedID getUserRequest(@RequestBody RequestDto requestDto) {
        System.out.println("Start: HTTPController");

        // здесь будет проверка добавления таски в бд и изъятие этой таски из бд
        {
            // как правильно настроить
            DataBaseEntry one = new DataBaseEntry("SomeStr", 10, "word",
                    WorkerStatus.READY, "hash", 5, 2345);
            taskRepository.insert(one);

            DataBaseEntry retrievedStudent = taskRepository.findById(one.getId()).orElse(null);
            if (retrievedStudent != null) {
                System.out.println("Retrieved Student: " + retrievedStudent.getHash());
            } else {
                System.out.println("Student not found!");
            }
        }

//        int objectiveID = addTask2Collection(createWorkerTask(requestDto));
//        mqController.sendTaskToQueue(getObjective(objectiveID));

        // это уже не понадобится, так как всё взаимодействие будет происходить через очередь.
        // invokeWorker(new HttpEntity<>(Objects.requireNonNull(getObjective(objectiveID))));
        System.out.println("End: HTTPController");

        return new RequestedID(RESERVED_ID);
    }

    @GetMapping
    public Response2User sendResult2User(@RequestParam("id") String id) {

        synchronized (collection) {
            // добавить синхронизацию
            for (Task task : collection) {
                // при нескольких тасках прога выводит первую таску из очереди
                // это из-за того, что я привязал к task.userID
                if (task.userID.equals(id)) {
                    double workingTimeSec = (System.currentTimeMillis() - task.creationTime) / 1000.0;
                    int TIME_LIMIT = 15;
                    if (task.status.equals(WorkerStatus.IN_PROGRESS) && workingTimeSec >= TIME_LIMIT) {
                        // здесь рассматривается случай, когда время таски превышено
                        // здесь ещё нужно отправить worker, чтобы он прекратил выполнение текущей задачи
                        // и при том НЕ завершился
                        return new Response2User(WorkerStatus.ERROR, null);
                    } else if (task.status.equals(WorkerStatus.IN_PROGRESS) && task.word == null) {
                        // здесь рассматривается случай, когда таска выполняется от 0 и до 15 сек.
                        return new Response2User(WorkerStatus.IN_PROGRESS, null);
                    }
                    // здесь нужно очистить нашу коллекцию тасок
                    deleteAllElementsFromTaskQueue(id);
                    return new Response2User(WorkerStatus.READY, task.word);
                }
            }
            return null;
        }
    }

    private void deleteAllElementsFromTaskQueue(String userID){
        collection.removeIf(buff -> buff.userID.equals(userID));
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
        // добавить синхронизацию
        synchronized (collection){
            for (Task task : collection) {
                synchronized (task){
                    if (task.taskID == id) {
                        return task;
                    }
                }
            }
            return null;
        }
    }
}
