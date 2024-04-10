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

    private final String RESERVED_ID = "730a04e6-4de9-41f9-9d5b-53b88b17afac";
    private final boolean ALL_TASKS_ARE_DONE = true;
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

        System.out.println("\uD83E\uDD17 End: HTTPController");
        return new RequestedID(RESERVED_ID);
    }

    @GetMapping
    public Response2User sendResult2User(@RequestParam("id") String userId){
        List<DataBaseEntry> tasksFromDB = taskRepository.findByUserID(userId);

        for (int i = 0; i < tasksFromDB.size(); i++) {
            DataBaseEntry currentTask = tasksFromDB.get(i);
            double workingTimeSec = (System.currentTimeMillis() - currentTask.getCreationTime()) / 1000.0;

            if (currentTask.getWord() != null && !currentTask.getWord().equals("non")){
                return new Response2User(WorkerStatus.READY, currentTask.getWord());
            }
            else if (currentTask.getStatus().equals(WorkerStatus.IN_PROGRESS) &&
                    workingTimeSec <= TIME_LIMIT &&
                    i == tasksFromDB.size() - 1){
                return new Response2User(WorkerStatus.IN_PROGRESS, "Your task is processed.");
            }
        }
        return new Response2User(WorkerStatus.ERROR, "Your time is overed.");
    }

    private boolean checkAllTasks(List<DataBaseEntry> tasksFromDB){
        return false;
    }

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
        //TODO: можно ли эту штуку вынести в DbController ? Не будет ли в DbController рекурсивная зависимость ?
        for (Task task : tasks) {
            dbController.saveTaskIntoDB(task);
        }
    }

}
