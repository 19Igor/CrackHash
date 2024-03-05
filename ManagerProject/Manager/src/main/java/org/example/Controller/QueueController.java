package org.example.Controller;

import lombok.AllArgsConstructor;
import org.example.Const.WorkerStatus;
import org.example.Model.Task;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("queue")
@AllArgsConstructor
public class QueueController {
    private CopyOnWriteArrayList<Task> collection;

    @PostMapping
    // синхронизовать
    //  java memory module(double check locking) (чтение несонхранизованного чтения)
    public void getTaskFromWorker(@RequestBody Task task){
        for (Task value : collection) {
            if (value.taskID == task.taskID) {
                // на этом моменте может быть уже изменено значение другим потоком
                value.status = WorkerStatus.READY;
                // или на этом моменте может быть уже изменено значение другим потоком
                value.word = task.word;
            }
        }
    }
}
