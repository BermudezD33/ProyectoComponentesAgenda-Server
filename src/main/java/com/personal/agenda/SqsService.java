package com.personal.agenda;

import com.amazonaws.services.sqs.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.config.annotation.NotificationMessage;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Service
public class SqsService {
    private static final String QUEUE_REQUEST = "ColaRequest.fifo";
    private static final String QUEUE_RESPONSE = "ColaResponse.fifo";

    public static final Logger LOGGER = LoggerFactory.getLogger(SqsService.class);

    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    public void sendMessageToSqs(final Message message) {
        LOGGER.info("Sending the message to the Amazon sqs.");
        queueMessagingTemplate.convertAndSend(QUEUE_RESPONSE, message);
        LOGGER.info("Message sent successfully to the Amazon sqs.");
    }

    @SqsListener(value = QUEUE_REQUEST, deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void getMessageFromSqs(String message, @Header("MessageId") String messageId) {
        LOGGER.info("Received message= {} with messageId= {}", message, messageId);

        System.out.println("Received message= " + message);
    }
}
