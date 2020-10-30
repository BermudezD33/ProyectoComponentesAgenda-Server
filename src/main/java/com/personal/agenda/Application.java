package com.personal.agenda;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.joda.time.DateTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.ConfigurableApplicationContext;
import software.amazon.ion.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        Evento escribir = new Evento();
        escribir.setFecha(new Long(20));
        escribir.setAsunto("Tabla de eventos");

        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        DynamoDBMapper dynamoDBMapper = context.getBean(DynamoDBMapper.class);
        dynamoDBMapper.save(escribir);
      //  Evento evento = dynamoDBMapper.load(Evento.class, "fa1508c4-3a7e-4054-8905-484226f92f0e");

     //   System.out.println(evento);



//        QueueMessagingTemplate queueMessagingTemplate = context.getBean(QueueMessagingTemplate.class);
//        Map<String, Object> headers = new HashMap<>();
//        headers.put("message-group-id", "G1");
//        headers.put("message-deduplication-id", "ID1");
//        queueMessagingTemplate.convertAndSend("ColaRequest.fifo", "{ \"id\": 1 }", headers);

    }


    public static void transformarJson(String mensaje){

          System.out.println(mensaje + "Hola");
    };

}