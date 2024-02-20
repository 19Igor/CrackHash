package org.example.Controller;

import lombok.AllArgsConstructor;
import org.example.Const.WorkerStatus;
import org.example.Model.Task;
import org.example.Model.TaskQueue;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("queue")
@AllArgsConstructor
public class QueueController {
    // сюда будут поступать только таски от воркеров

    @PostMapping
    public void getTaskFromWorker(@RequestBody Task task){
        // здесь мы кладём таску в очередь, respectively говорим, что таска выполнена
        for (int i = 0; i < TaskQueue.taskQueue.size(); i++)
        {
            if (TaskQueue.taskQueue.get(i).taskID == task.taskID)
            {
                TaskQueue.taskQueue.get(i).status = WorkerStatus.READY;
                TaskQueue.taskQueue.get(i).word = task.word;
            }
        }
    }
}
