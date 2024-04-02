package org.example.Model;


import jakarta.xml.bind.annotation.XmlRootElement;
import org.example.Const.WorkerStatus;


@XmlRootElement
public class Task {
//    @XmlElement(name = "userID")
    public String userID;
//    @XmlElement(name = "taskID")
    public int taskID;
//    @XmlElement(name = "word")
    public String word = null;
//    @XmlElement(name = "status")
    public WorkerStatus status = null;
//    @XmlElement(name = "hash")
    public String hash = null;
//    @XmlElement(name = "maxLength")
    public int maxLength;
//    @XmlElement(name = "creationTime")
    public long creationTime;
}
