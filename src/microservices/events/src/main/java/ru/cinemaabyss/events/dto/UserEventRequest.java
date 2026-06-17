package ru.cinemaabyss.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record UserEventRequest(
        @JsonProperty("user_id")
        Integer userId,
        String username,
        String email,
        String action,
        Instant timestamp
) {
}