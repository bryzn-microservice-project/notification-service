package com.kafka;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;
import com.SchemaService;

/*
 * Responsible handling all kafka related operations such as producing and comnsuming topics from
 * the bus
 */
@Configuration
public class KafkaConfig {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaService.class);
    
    @Bean
    public KafkaTemplate<Void, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<Void, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    /*
     * Configures the producer with the necessary properties.
     * This is where you can add more configurations as needed.
     * NOTE: the application.properties/compose yaml configurations
     * override these properties.
     */
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        LOG.info("[KAFKA] Configuring Kafka producer (kafka:9092)");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // More configs can go here
        return props;
    }

    @Bean
    public CommonErrorHandler errorHandler(KafkaOperations<?, ?> template) {
        return new DefaultErrorHandler(new DeadLetterPublishingRecoverer(template),
                new FixedBackOff(1000L, 2));
    }

    /*
     * Creates Kafka topics based on the SchemaService enum. 
     * NOTE: This method doesnt create duplicate topics if they exist
     * so I can have this also in the gui service
     */
    @Bean
    public List<NewTopic> createKafkaTopics() {
        List<NewTopic> topics = Arrays.stream(SchemaService.values())
                .map(topicType -> new NewTopic(topicType.getTopicName(), 1, (short) 1)).toList();
        topics.forEach(topic -> LoggerFactory.getLogger(KafkaConfig.class)
                .info("[KAFKA] Creating Kafka topic: " + topic.name()));
        return topics;
    }
}

