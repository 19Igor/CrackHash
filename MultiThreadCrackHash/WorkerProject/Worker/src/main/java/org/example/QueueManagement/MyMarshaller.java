package org.example.QueueManagement;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.Setter;
import org.example.Model.Task;
import org.springframework.context.annotation.Scope;

import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayDeque;


@Component
@Setter
@Scope("prototype")
public class MyMarshaller {

    private static final String FILE_NAME = "MarshallerBean.xml";
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    public MyMarshaller(){}

    public Task convertXmlString2Task(String xmlTask){
        Task buff = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Task.class);
            jakarta.xml.bind.Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            buff = (Task) unmarshaller.unmarshal(new StringReader(xmlTask));
            System.out.println(buff.hash);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        return buff;
    }

    public String convertTask2XmlString(Task task){
        StringWriter stringWriter = new StringWriter();

        try{
            JAXBContext buff = JAXBContext.newInstance(Task.class);
            jakarta.xml.bind.Marshaller marshaller = buff.createMarshaller();
            marshaller.marshal(task, stringWriter);
            System.out.println(stringWriter);
        }
        catch (JAXBException e){
            e.printStackTrace();
        }
        return stringWriter.toString();
    }

}
