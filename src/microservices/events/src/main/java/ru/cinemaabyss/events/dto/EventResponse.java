package ru.cinemaabyss.events.dto;

public record EventResponse(
        String status,
        Integer partition,
        Long offset,
        EventMessage event
) {
}