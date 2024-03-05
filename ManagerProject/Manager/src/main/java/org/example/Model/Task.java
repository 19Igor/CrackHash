package org.example.Model;


import lombok.Data;
import org.example.Const.WorkerStatus;

@Data
// синхронизовать
public class Task {
    public String userID;
    public int taskID;
    public String word = null;
    public WorkerStatus status = null;
    public String hash = null;
    public int maxLength;
    public long creationTime;
}
