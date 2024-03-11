package org.example;

import org.example.Const.WorkerStatus;
import org.example.Model.Task;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

@SpringBootApplication
public class Start {

    public Start(){
    }
    public static void main(String[] args){
        SpringApplication.run(Start.class, args);
    }
}
