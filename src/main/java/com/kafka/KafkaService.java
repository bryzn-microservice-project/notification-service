package com.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/*
 * Responsible handling all kafka related operations such as producing and comnsuming topics from
 * the bus
 */
@Service
public class KafkaService {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaService.class);

    private final KafkaTemplate<Void, String> kafkaTemplate;

    public KafkaService(KafkaTemplate<Void, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /*
     * Method to publish a topic to the Kafka bus. takes in the topic name and the
     * json string representation of the topic.
     */
    public void publishTopic(String topicName, String jsonTopic) {
        LOG.info("[KAFKA] Publishing topic: " + topicName);
        kafkaTemplate.send(topicName, jsonTopic);
    }
}

