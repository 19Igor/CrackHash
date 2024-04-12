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
import java.util.UUID;

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
    private final DbService dbService;
    @Autowired
    private final RabbitMqController rabbitMqController;
    @Autowired
    private final ObjectiveDistribution objectiveDistribution;

    private static int currentTaskCounter = 0;

    @PostMapping()
    // точка старта
    public RequestedID getUserRequest(@RequestBody RequestDto requestDto) {
        System.out.println("\uD83E\uDD17 Start: HTTPController");

        String userId = UUID.randomUUID().toString();
        Task task = createGeneralTask(requestDto, userId);
        List<Task> entries = objectiveDistribution.distributeObjectives(task);

        dbService.sendTasksIntoDB(entries);
        rabbitMqController.queueTasks(entries);

        System.out.println("\uD83E\uDD17 End: HTTPController");

        return new RequestedID(userId);
    }

    @GetMapping
    public Response2User sendResult2User(@RequestParam("id") String userId){
        List<DataBaseEntry> tasksFromDB = taskRepository.findByUserID(userId);

        for (int i = 0; i < tasksFromDB.size(); i++) {
            DataBaseEntry currentTask = tasksFromDB.get(i);
            double workingTimeSec = (System.currentTimeMillis() - currentTask.getCreationTime()) / 1000.0;

            // убрать условие && !currentTask.getWord().equals("non")
            if (currentTask.getWord() != null){
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

    private Task createGeneralTask(RequestDto requestBody, String userId){

        Task newTask = new Task();
        newTask.userID = userId;
        newTask.taskID = currentTaskCounter++;      // 0
        newTask.status = WorkerStatus.IN_PROGRESS;
        newTask.hash = requestBody.getHash();
        newTask.length = requestBody.getMaxLength();
        newTask.creationTime = System.currentTimeMillis();
        newTask.firstWord = '?';
        newTask.lastWord = '?';

        return newTask;
    }


}
