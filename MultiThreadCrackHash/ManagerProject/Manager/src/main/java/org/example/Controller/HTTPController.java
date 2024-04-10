package org.example.Controller;

import lombok.AllArgsConstructor;
import org.example.Const.WorkerStatus;
import org.example.DbManagement.TaskRepository;
import org.example.Model.*;
import org.example.QueueManagement.RabbitMqController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.example.Const.Constants.TIME_LIMIT;

@RestController
@RequestMapping("manager")
@AllArgsConstructor
@EnableAsync
@EnableScheduling
@EnableMongoRepositories(basePackages = {"org.example.DbManagement"})
public class HTTPController {

    @Autowired
    private final TaskRepository taskRepository;
    @Autowired
    private final DbController dbController;
    @Autowired
    private final RabbitMqController rabbitMqController;
    @Autowired
    private final ObjectiveDistribution objectiveDistribution;

    private final CopyOnWriteArrayList<Task> collection;
    private final String RESERVED_ID = "730a04e6-4de9-41f9-9d5b-53b88b17afac";
    private static int currentTaskCounter = 0;

    @PostMapping()
    // точка старта
    public RequestedID getUserRequest(@RequestBody RequestDto requestDto) {
        System.out.println("\uD83E\uDD17 Start: HTTPController");

        Task task = createGeneralTask(requestDto);
        List<Task> entries = objectiveDistribution.distributeObjectives(task);

        taskRepository.deleteAll();
        sendTasksIntoDB(entries);
        rabbitMqController.queueTasks(entries);

        // It needs to send mini-tasks through the queue to workers.


//        List<DataBaseEntry> retrievedTasks = taskRepository.findByUserID(RESERVED_ID);
//        System.out.println("\uD83D\uDE0E\uD83D\uDC4C\uD83D\uDD25 Output of dataBaseEntries:");
//        for (DataBaseEntry entry : retrievedTasks){
//            System.out.println(entry.getFirstWord() + " - " + entry.getLastWord());
//        }

        // здесь будет проверка добавления таски в бд и изъятие этой таски из бд
        {
//            taskRepository.deleteAll();
//            DataBaseEntry one = new DataBaseEntry("SomeStr", 10, "word",
//                    WorkerStatus.READY, "hash", 5, 2345, 'a', 'b');
//            taskRepository.insert(one);
//
//            List<DataBaseEntry> retrievedTasks = taskRepository.findByUserID("SomeStr");
//            if (retrievedTasks != null) {
//                System.out.println("\uD83E\uDD17 Это длина retrievedTasks: " + retrievedTasks.size());
//                System.out.println("Retrieved Entry: " + retrievedTasks.get(0).getWord());
//            } else {
//                System.out.println("\uD83D\uDE14 The entry not found!");
//            }
        }

//        mqController.sendTaskToQueue(getObjective(objectiveID));

        System.out.println("\uD83E\uDD17 End: HTTPController");
        return new RequestedID(RESERVED_ID);
    }

    @GetMapping
    public Response2User sendResult2User(@RequestParam("id") String userId){
        List<DataBaseEntry> tasksFromDB = taskRepository.findByUserID(userId);

        for(DataBaseEntry entry: tasksFromDB){
            if (entry.getWord() != null && !entry.getWord().equals("non")){
                return new Response2User(WorkerStatus.READY, entry.getWord());
            }
            else if (entry.getStatus().equals(WorkerStatus.IN_PROGRESS) && entry.getCreationTime() <= TIME_LIMIT) {
                return new Response2User(WorkerStatus.IN_PROGRESS, "Your task is processed.");
            }
        }
        return new Response2User(WorkerStatus.ERROR, "Your time is overed.");
    }

//    @GetMapping
//    public Response2User sendResult2User(@RequestParam("id") String id) {
//
//        synchronized (collection) {
//            // добавить синхронизацию
//            for (Task task : collection) {
//                // при нескольких тасках прога выводит первую таску из очереди
//                // это из-за того, что я привязал к task.userID
//                if (task.userID.equals(id)) {
//                    double workingTimeSec = (System.currentTimeMillis() - task.creationTime) / 1000.0;
//                    int TIME_LIMIT = 15;
//                    if (task.status.equals(WorkerStatus.IN_PROGRESS) && workingTimeSec >= TIME_LIMIT) {
//                        // здесь рассматривается случай, когда время таски превышено
//                        // здесь ещё нужно отправить worker, чтобы он прекратил выполнение текущей задачи
//                        // и при том НЕ завершился
//                        return new Response2User(WorkerStatus.ERROR, null);
//                    } else if (task.status.equals(WorkerStatus.IN_PROGRESS) && task.word == null) {
//                        // здесь рассматривается случай, когда таска выполняется от 0 и до 15 сек.
//                        return new Response2User(WorkerStatus.IN_PROGRESS, null);
//                    }
//                    // здесь нужно очистить нашу коллекцию тасок
//                    deleteAllElementsFromTaskQueue(id);
//                    return new Response2User(WorkerStatus.READY, task.word);
//                }
//            }
//            return null;
//        }
//    }

    private Task createGeneralTask(RequestDto requestBody){

        Task newTask = new Task();
        newTask.userID = RESERVED_ID;
        newTask.taskID = currentTaskCounter++;      // 0
        newTask.status = WorkerStatus.IN_PROGRESS;
        newTask.hash = requestBody.getHash();
        newTask.length = requestBody.getMaxLength();
        newTask.creationTime = System.currentTimeMillis();
        newTask.firstWord = '?';
        newTask.lastWord = '?';

        return newTask;
    }

    private void sendTasksIntoDB(List<Task> tasks){
        // можно ли эту штуку вынести в DbController ? Не будет ли в DbController рекурсивная зависимость ?
        for (Task task : tasks) {
            dbController.saveTaskIntoDB(task);
        }
    }

}
