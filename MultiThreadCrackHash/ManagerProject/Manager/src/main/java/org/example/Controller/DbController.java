package org.example.Controller;

import lombok.AllArgsConstructor;
import org.example.DbManagement.TaskRepository;
import org.example.Model.DataBaseEntry;
import org.example.Model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Component
@RestController
@AllArgsConstructor
public class DbController {
    @Autowired
    private final TaskRepository taskRepository;

    public void saveTaskIntoDB(final Task task){
        DataBaseEntry one = convertTask2DataBaseEntry(task);
        System.out.println("\uD83D\uDE09 It's one: " + one.getId() + " and fist word: " + one.getFirstWord());
        taskRepository.insert(one);
    }

    public void updateTaskIntoDB(Task task){
        taskRepository.save(convertTask2DataBaseEntry(task));
    }

    private DataBaseEntry convertTask2DataBaseEntry(Task task){
        return new DataBaseEntry(
                task.userID, task.taskID,
                task.word, task.status,
                task.hash, task.length,
                task.creationTime,
                task.firstWord, task.lastWord);
    }
}
