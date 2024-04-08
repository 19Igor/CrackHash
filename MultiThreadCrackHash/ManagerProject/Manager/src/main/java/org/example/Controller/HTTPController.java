package org.example.Controller;

import lombok.AllArgsConstructor;
import org.example.Const.WorkerStatus;
import org.example.DbManagement.TaskRepository;
import org.example.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("manager")
@AllArgsConstructor
@EnableAsync
@EnableScheduling
@EnableMongoRepositories(basePackages = {"org.example.DbManagement"})
public class HTTPController {

    private final CopyOnWriteArrayList<Task> collection;
    private final String RESERVED_ID = "730a04e6-4de9-41f9-9d5b-53b88b17afac";
    private static int currentTaskCounter = 0;
    @Autowired
    private final TaskRepository taskRepository;
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz1234567890"; // (36)
    private static final int workerAmount = 2;


    @PostMapping()
    // точка старта
    public RequestedID getUserRequest(@RequestBody RequestDto requestDto) {
        System.out.println("\uD83E\uDD17 Start: HTTPController");

//        Task task = createWorkerTask(requestDto);
//        appendTask2DB(task);


        // здесь будет проверка добавления таски в бд и изъятие этой таски из бд
        {
            taskRepository.deleteAll();
            DataBaseEntry one = new DataBaseEntry("SomeStr", 10, "word",
                    WorkerStatus.READY, "hash", 5, 2345, 'a', 'b');
            taskRepository.insert(one);

//            DataBaseEntry retrievedStudent = taskRepository.findById(one.getId()).orElse(null);
//            List<DataBaseEntry> retrievedTasks = taskRepository.findDataBaseEntriesByUserID(RESERVED_ID);

            List<DataBaseEntry> retrievedTasks = taskRepository.findByUserID("SomeStr");
            if (retrievedTasks != null) {
                System.out.println("\uD83E\uDD17 Это длина retrievedTasks: " + retrievedTasks.size());
                System.out.println("Retrieved Entry: " + retrievedTasks.get(0).getWord());
            } else {
                System.out.println("\uD83D\uDE14 The entry not found!");
            }
        }

//        mqController.sendTaskToQueue(getObjective(objectiveID));

        // это уже не понадобится, так как всё взаимодействие будет происходить через очередь.
        // invokeWorker(new HttpEntity<>(Objects.requireNonNull(getObjective(objectiveID))));
        System.out.println("End: HTTPController");

        return new RequestedID(RESERVED_ID);
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

//    @GetMapping
//    public Response2User sendResult2User(@RequestParam("id") String id) {
//        DataBaseEntry retrievedStudent = taskRepository.findBy()
//    }

    private void deleteAllElementsFromTaskQueue(String userID){
        collection.removeIf(buff -> buff.userID.equals(userID));
    }


    private Task createWorkerTask(RequestDto requestBody){
        char[] alphabet = ALPHABET.toCharArray();

        Task newTask = new Task();
        newTask.userID = RESERVED_ID;
        newTask.taskID = currentTaskCounter++;      // 0
        newTask.status = WorkerStatus.IN_PROGRESS;
        newTask.hash = requestBody.getHash();
        newTask.length = requestBody.getMaxLength();
        newTask.creationTime = System.currentTimeMillis();
        newTask.firstWord = getFirstWord(alphabet, newTask.taskID);
        newTask.lastWord = getLastWord(alphabet, newTask.taskID);

        return newTask;
    }

    private char getFirstWord(char[] alphabet, int currentTaskCounter){
        int shift = alphabet.length / workerAmount;
        return alphabet[shift * currentTaskCounter];
    }

    private char getLastWord(char[] alphabet, int currentTaskCounter){
        int shift = alphabet.length / workerAmount;
        if (shift * currentTaskCounter + shift > alphabet.length)
            return alphabet[alphabet.length - 1];
        return alphabet[shift * currentTaskCounter + shift - 1];
    }

    private void appendTask2DB(Task task){
        DataBaseEntry dataBaseEntry = new DataBaseEntry(
                task.userID,
                task.taskID,
                task.word,
                task.status,
                task.hash,
                task.length,
                task.creationTime,
                'a', 'r' // переделать
        );

        // сюда нужно накинуть синхронизацию
        taskRepository.insert(dataBaseEntry);
    }
}
