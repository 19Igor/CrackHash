package org.example.Model;

import lombok.Data;

@Data
public class RequestedID {
    private String requestId;

    public RequestedID(String requestId) {
        this.requestId = requestId;
    }
}
