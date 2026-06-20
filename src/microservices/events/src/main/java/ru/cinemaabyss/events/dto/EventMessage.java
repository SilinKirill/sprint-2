package ru.cinemaabyss.events.dto;

import java.time.Instant;

public record EventMessage(
        String id,
        String type,
        Instant timestamp,
        Object payload
) {
}