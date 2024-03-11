package org.example.QueueManagement;

import lombok.Setter;
import org.example.Model.Task;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Component;

import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;

@Setter
@Component
@Scope("prototype")
public class MyMarshaller {

    private static final String FILE_NAME = "MarshallerBean.xml";

    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    public MyMarshaller() {
    }

    String convertTask2XmlString(Task task){
        ApplicationContext appContext = new ClassPathXmlApplicationContext(FILE_NAME);
        MyMarshaller application = (MyMarshaller) appContext.getBean("myMarshaller");

        StringWriter stringWriter = new StringWriter();
        try {
            application.marshaller.marshal(task, new StreamResult(stringWriter));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        System.out.println("It's the string: " + stringWriter);
        return stringWriter.toString();
    }


}
