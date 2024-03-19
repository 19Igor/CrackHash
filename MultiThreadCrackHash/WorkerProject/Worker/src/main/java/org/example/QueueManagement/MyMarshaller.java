package org.example.QueueManagement;

import lombok.Setter;
import org.example.Model.Task;
import org.springframework.context.annotation.Scope;

import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Component;

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


        { // Удалить
//            ApplicationContext appContext = null;
//            try {
//
//
//                appContext = new ClassPathXmlApplicationContext(FILE_NAME);
//                MyMarshaller application = (MyMarshaller) appContext.getBean("myNewMarshaller");
//                StringReader reader = new StringReader(xmlTask);
//                Task buff1 = (Task) application.unmarshaller.unmarshal(new StreamSource(reader));
//                return buff1;
//
//
//            } catch (Exception e) {
//                System.out.println("По ... пошла загрузка бина");
//                e.printStackTrace();
//            }
        }

        return null;
    }
}
