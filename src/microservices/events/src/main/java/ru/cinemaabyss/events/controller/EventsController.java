package ru.cinemaabyss.events.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.cinemaabyss.events.dto.EventResponse;
import ru.cinemaabyss.events.dto.MovieEventRequest;
import ru.cinemaabyss.events.dto.PaymentEventRequest;
import ru.cinemaabyss.events.dto.UserEventRequest;
import ru.cinemaabyss.events.service.EventService;

import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventsController {

    private final EventService eventService;

    public EventsController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/health")
    public Map<String, Boolean> health() {
        return Map.of("status", true);
    }

    @PostMapping("/movie")
    public ResponseEntity<EventResponse> createMovieEvent(@RequestBody MovieEventRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventService.createMovieEvent(request));
    }

    @PostMapping("/user")
    public ResponseEntity<EventResponse> createUserEvent(@RequestBody UserEventRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventService.createUserEvent(request));
    }

    @PostMapping("/payment")
    public ResponseEntity<EventResponse> createPaymentEvent(@RequestBody PaymentEventRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventService.createPaymentEvent(request));
    }
}