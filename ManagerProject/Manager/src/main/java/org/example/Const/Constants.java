package org.example.Const;

import org.springframework.stereotype.Component;

@Component
public class Constants {
    public final String localURL = "http://localhost:8081/queue";
    public final String serviceURL = "http://worker-service:8081/queue";
}
