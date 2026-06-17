package ru.cinemaabyss.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentEventRequest(
        @JsonProperty("payment_id")
        Integer paymentId,
        @JsonProperty("user_id")
        Integer userId,
        BigDecimal amount,
        String status,
        Instant timestamp,
        @JsonProperty("method_type")
        String methodType
) {
}