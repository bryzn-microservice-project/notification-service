package com.controller;

import java.io.InputStream;
import java.net.URL;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.SchemaService;
import com.businessLogic.BusinessLogic;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.KafkaService;
import com.schema.SchemaValidator;

@RestController
public class MainController {
    private SchemaValidator schemaValidator;
    private BusinessLogic businessLogic;
    private KafkaService kafkaService;
    private long lifetimeMins = 0;
    private static final Logger LOG = LoggerFactory.getLogger(BusinessLogic.class);

    public MainController(SchemaValidator schemaValidator, BusinessLogic businessLogic, KafkaService kafkaService) {
        this.schemaValidator = schemaValidator;
        this.businessLogic = businessLogic;
        this.kafkaService = kafkaService;
    }

    @GetMapping("/api/v1/name")
    public String microserviceName() {
        return "This microservice is the [NOTIFICATION-SERVICE]!";
    }

    /*
     * Main entry point for processing incoming topics other microservices will use this enpoint
     */
    @PostMapping("/api/v1/processTopic")
    public void processRestTopics(@RequestBody String jsonString) {
        LOG.info("Received an incoming topic... Processing now!");
        System.out.println("\n\nJSON: " + jsonString + "\n\n");
        ObjectMapper mapper = new ObjectMapper();
        JSONObject jsonNode = new JSONObject(jsonString);
        String topicName = jsonNode.getString("topicName");
        URL schemaUrl =
                getClass().getClassLoader().getResource(SchemaService.getPathFor(topicName));
        LOG.info("Schema URL: " + schemaUrl);
        InputStream schemaStream = null;
        try {
            schemaStream = schemaValidator.getSchemaStream(SchemaService.getPathFor(topicName));
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        if (schemaStream == null) {
            LOG.error("No schema found for topic: " + topicName);
        }

        if (schemaValidator.validateJson(schemaStream, jsonNode)) {
            try {
                switch (jsonNode.getString("topicName")) {
                    case "LoginResponse":
                    case "PaymentResponse":
                    case "RewardsResponse":
                    case "NewAccountResponse": {
                        businessLogic.processNotification(topicName);
                        // Publish the topic to Kafka after processing for the GUI
                        kafkaService.publishTopic(topicName, jsonString);
                    }
                        break;
                    default: {
                        LOG.warn("Non-supported Topic: " + topicName);
                    }
                        break;
                }
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }
        } else {
            LOG.error("Failed schema validation...");
        }
    }

    // Push notitications every minute
    @Scheduled(fixedRate = 60000) // every 60 seconds
    public void pushNotifications(){
        ObjectMapper mapper = new ObjectMapper();
        String notifications = "";
        // this prints when the service first boots up, therefore increment post-initialization
        LOG.info("Pushing notifications... I have been alive for " + (lifetimeMins++) + " minutes.");
        try {
            notifications = mapper.writeValueAsString(businessLogic.createNotifcations().toString());
            kafkaService.publishTopic("Notifications", notifications);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

}
