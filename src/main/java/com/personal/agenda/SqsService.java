package com.personal.agenda;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SqsService {
    private static final String QUEUE_REQUEST = "ColaRequest.fifo";
    private static final String QUEUE_RESPONSE = "ColaResponse.fifo";

    public static final Logger LOGGER = LoggerFactory.getLogger(SqsService.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    public void sendMessageToSqs(String message) {
        System.out.println("Sending the message to the Amazon sqs.");
        Map<String, Object> headers = new HashMap<>();
        headers.put("message-group-id", "G1");
        headers.put("message-deduplication-id", String.valueOf(new Date().getTime()));
        queueMessagingTemplate.convertAndSend(QUEUE_RESPONSE, message, headers);
        System.out.println("Message sent successfully to the Amazon sqs.");
    }

    @SqsListener(value = QUEUE_REQUEST, deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void getMessageFromSqs(String message, @Header("MessageId") String messageId) throws Exception {
        System.out.println("Mensaje recibido: " + message);

        Mensaje mensaje = objectMapper.readValue(message, Mensaje.class);
        String retornable;
        System.out.println(mensaje.toString());
        try {
            switch (mensaje.getTipoMensaje()) {
                case "Create":

                    dynamoDBMapper.save(mensaje.getEvento());
                    System.out.println("Guardado");
                    break;
                case "Retrieve":
                    if (mensaje.getEvento().getId() != null) {
                        Evento event = dynamoDBMapper.load(Evento.class, mensaje.getEvento().getId());
                        System.out.println("Retrieved" + event);
                    }
                    break;
                case "RetrieveByDay":
                    if (mensaje.getEvento() != null) {
                        Map<String, AttributeValue> eav = new HashMap<>();
                        eav.put(":fechaIni", new AttributeValue().withN("0"));
                        eav.put(":fechaFin", new AttributeValue().withN(String.valueOf(new Date().getTime())));
                        DynamoDBScanExpression scanExpression =
                                new DynamoDBScanExpression()
                                    .withFilterExpression("Fecha BETWEEN :fechaIni AND :fechaFin")
                                    .withExpressionAttributeValues(eav);
                        List<Evento> eventos = dynamoDBMapper.scan(Evento.class, scanExpression);
                        mensaje.setEventos(eventos);
                    }
                    break;
                case "Update":
                    if (mensaje.getEvento().getId() != null) {
                        dynamoDBMapper.save(mensaje.getEvento());
                        System.out.println("Actualizado");
                    }
                    break;
                case "Delete":
                    if (mensaje.getEvento().getId() != null) {
                        dynamoDBMapper.delete(mensaje.getEvento());
                        System.out.println("Borrado");
                    }
                    break;
                default:
                    throw new Exception("No se pude realizar ninguna accion con ese mensaje");
            }
            mensaje.setResultado("Success");


        } catch (Exception exception) {
            mensaje.setResultado("Fail");
        }
        retornable = objectMapper.writeValueAsString(mensaje);
        System.out.println(retornable);

        this.sendMessageToSqs(retornable);
    }
}
