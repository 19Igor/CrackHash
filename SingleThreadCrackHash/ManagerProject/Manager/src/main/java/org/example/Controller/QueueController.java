package org.example.Controller;

import lombok.AllArgsConstructor;
import org.example.Const.WorkerStatus;
import org.example.Model.Task;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("queue")
@AllArgsConstructor
public class QueueController {
    private final CopyOnWriteArrayList<Task> collection;

    @Async
    @PostMapping
    //  java memory module(double check locking) (чтение несонхранизованного чтения)
    public void getTaskFromWorker(@RequestBody Task task){
        /*
        * Данный метод не являетяс атомарным, так как в моменте между поиском и присваиванием значений, состояние
        * найденного элемента может поменяться.
        * */

        synchronized (collection){
            for (Task value : collection) {
                if (value.taskID == task.taskID) {
                    value.status = WorkerStatus.READY;
                    value.word = task.word;
                }
            }
        }


//        for (Task value : collection) {
//            if (value.taskID == task.taskID) {
//                // на этом моменте может быть уже изменено значение другим потоком
//                value.status = WorkerStatus.READY;
//                // или на этом моменте может быть уже изменено значение другим потоком
//                value.word = task.word;
//            }
//        }
    }
}
