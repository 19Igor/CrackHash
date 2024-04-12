package org.example.Model;

import lombok.Data;
import org.example.Const.WorkerStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "Tasks")
public class DataBaseEntry {
    @Id
    private String id; // проверить запуск без id
    @Field("userID")
    private String userID;
    @Field("taskID")
    private int taskID;
    @Field("word")
    private String word = null;
    @Field("status")
    private WorkerStatus status = null;
    @Field("hash")
    private String hash = null;
    @Field("maxLength")
    private int maxLength;
    @Field("creationTime")
    private long creationTime;
    @Field("firstWord")
    private char firstWord;
    @Field("lastWord")
    private char lastWord;

    public DataBaseEntry(String userID, int taskID, String word,
                         WorkerStatus status, String hash,
                         int maxLength, long creationTime, char firstWord, char lastWord) {
        this.userID = userID;
        this.taskID = taskID;
        this.word = word;
        this.status = status;
        this.hash = hash;
        this.maxLength = maxLength;
        this.creationTime = creationTime;
        this.firstWord = firstWord;
        this.lastWord = lastWord;
    }
}
