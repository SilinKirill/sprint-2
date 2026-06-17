package ru.cinemaabyss.proxy.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "proxy")
public record ProxyProperties(
        String monolithUrl,
        String moviesServiceUrl,
        String eventsServiceUrl,
        boolean gradualMigration,
        int moviesMigrationPercent
) {
}