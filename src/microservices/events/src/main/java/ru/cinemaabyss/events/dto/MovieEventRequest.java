package ru.cinemaabyss.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MovieEventRequest(
        @JsonProperty("movie_id")
        Integer movieId,
        String title,
        String action,
        @JsonProperty("user_id")
        Integer userId,
        Double rating,
        List<String> genres,
        String description
) {
}