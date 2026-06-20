package ru.cinemaabyss.events.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {

    private static final Logger log = LoggerFactory.getLogger(EventConsumer.class);

    @KafkaListener(topics = {
            "${app.kafka.movie-events-topic}",
            "${app.kafka.user-events-topic}",
            "${app.kafka.payment-events-topic}"
    })
    public void consume(
            String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic
    ) {
        log.info("Event received from Kafka topic {}: {}", topic, message);
    }
}