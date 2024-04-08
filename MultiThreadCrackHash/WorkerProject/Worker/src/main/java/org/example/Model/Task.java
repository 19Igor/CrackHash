package org.example.Model;

import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import org.example.Const.WorkerStatus;


@XmlRootElement
public class Task {
    public String userID;
    public int taskID;
    public String word = null;
    public WorkerStatus status = null;
    public String hash = null;
    public int maxLength;
    public long creationTime;
    public char firstWord;
    public char lastWord;
}
