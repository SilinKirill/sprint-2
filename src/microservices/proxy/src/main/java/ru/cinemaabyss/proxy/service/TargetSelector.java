package ru.cinemaabyss.proxy.service;

import org.springframework.stereotype.Component;
import ru.cinemaabyss.proxy.properties.ProxyProperties;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class TargetSelector {

    private static final String MONOLITH = "monolith";
    private static final String MOVIES_SERVICE = "movies-service";
    private static final String EVENTS_SERVICE = "events-service";

    private final ProxyProperties properties;

    public TargetSelector(ProxyProperties properties) {
        this.properties = properties;
    }

    public Target select(String path) {
        if (path.startsWith("/api/events")) {
            return eventsTarget();
        }

        if (path.startsWith("/api/movies")) {
            return selectMoviesTarget();
        }

        return monolithTarget();
    }

    private Target selectMoviesTarget() {
        if (properties.gradualMigration() && isMoviesServiceSelected()) {
            return moviesTarget();
        }

        return monolithTarget();
    }

    private boolean isMoviesServiceSelected() {
        return ThreadLocalRandom.current().nextInt(100) < moviesMigrationPercent();
    }

    private int moviesMigrationPercent() {
        return Math.max(0, Math.min(100, properties.moviesMigrationPercent()));
    }

    private Target monolithTarget() {
        return new Target(MONOLITH, properties.monolithUrl());
    }

    private Target moviesTarget() {
        return new Target(MOVIES_SERVICE, properties.moviesServiceUrl());
    }

    private Target eventsTarget() {
        return new Target(EVENTS_SERVICE, properties.eventsServiceUrl());
    }

    public record Target(String name, String baseUrl) {
    }
}