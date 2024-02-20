package org.example.Model;

import java.util.concurrent.CopyOnWriteArrayList;

public class TaskQueue {

    public static CopyOnWriteArrayList<Task> taskQueue = new CopyOnWriteArrayList<>();
    private static volatile TaskQueue instance;
    private TaskQueue() {}
    public static TaskQueue getInstance(){
        TaskQueue localInstance = instance;
        if (localInstance == null){
            synchronized (TaskQueue.class){
                localInstance = instance;
                if (localInstance == null){
                    instance = localInstance = new TaskQueue();
                }
            }
        }
        return localInstance;
    }
}
