package com.personal.agenda;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        DynamoDBMapper dynamoDBMapper = context.getBean(DynamoDBMapper.class);
        Evento evento = dynamoDBMapper.load(Evento.class, "cf6cc989-e254-4429-bc8a-ebbab38a30e1");

        System.out.println(evento);
    }

}