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

                value.status = WorkerStatus.READY;

                value.word = task.word;
            }
        }

//        for (int i = 0; i < TaskQueue.taskQueue.size(); i++)
//        {
//            if (TaskQueue.taskQueue.get(i).taskID == task.taskID)
//            {
//                TaskQueue.taskQueue.get(i).status = WorkerStatus.READY;
//
//                TaskQueue.taskQueue.get(i).word = task.word;
//            }
//        }
    }
}
