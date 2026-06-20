package ru.cinemaabyss.events.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.cinemaabyss.events.dto.EventMessage;
import ru.cinemaabyss.events.dto.EventResponse;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class EventService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String movieEventsTopic;
    private final String userEventsTopic;
    private final String paymentEventsTopic;

    public EventService(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${app.kafka.movie-events-topic}") String movieEventsTopic,
            @Value("${app.kafka.user-events-topic}") String userEventsTopic,
            @Value("${app.kafka.payment-events-topic}") String paymentEventsTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.movieEventsTopic = movieEventsTopic;
        this.userEventsTopic = userEventsTopic;
        this.paymentEventsTopic = paymentEventsTopic;
    }

    public EventResponse createMovieEvent(Object payload) {
        return createEvent("movie", payload, movieEventsTopic);
    }

    public EventResponse createUserEvent(Object payload) {
        return createEvent("user", payload, userEventsTopic);
    }

    public EventResponse createPaymentEvent(Object payload) {
        return createEvent("payment", payload, paymentEventsTopic);
    }

    private EventResponse createEvent(String type, Object payload, String topic) {
        EventMessage event = new EventMessage(
                buildEventId(type),
                type,
                Instant.now(),
                payload
        );

        RecordMetadata metadata = sendEvent(topic, event);

        return new EventResponse(
                "success",
                metadata.partition(),
                metadata.offset(),
                event
        );
    }

    private RecordMetadata sendEvent(String topic, EventMessage event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            SendResult<String, String> result = kafkaTemplate
                    .send(topic, event.id(), message)
                    .get();

            return result.getRecordMetadata();
        } catch (JsonProcessingException | ExecutionException e) {
            throw new IllegalStateException("Failed to send event to Kafka", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Failed to send event to Kafka", e);
        }
    }

    private String buildEventId(String type) {
        return type + "-" + UUID.randomUUID();
    }
}