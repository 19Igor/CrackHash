package org.example.Model;

import lombok.Data;
import org.example.Const.WorkerStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class DataBaseEntry {
    @Id
    private int id;
    private String userID;
    private int taskID;
    private String word = null;
    private WorkerStatus status = null;
    private String hash = null;
    private int maxLength;
    private long creationTime;

    public DataBaseEntry(String userID, int taskID, String word,
                         WorkerStatus status, String hash,
                         int maxLength, long creationTime) {
        this.userID = userID;
        this.taskID = taskID;
        this.word = word;
        this.status = status;
        this.hash = hash;
        this.maxLength = maxLength;
        this.creationTime = creationTime;
    }
}
