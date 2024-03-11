package org.example.Model;

import lombok.Data;
import org.example.Const.WorkerStatus;

@Data
public class Response2User {
    WorkerStatus status;
    String data;

    public Response2User() {
    }

    public Response2User(WorkerStatus status, String data) {
        this.status = status;
        this.data = data;
    }
}
