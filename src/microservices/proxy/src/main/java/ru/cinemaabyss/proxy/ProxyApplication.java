package ru.cinemaabyss.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.cinemaabyss.proxy.properties.ProxyProperties;

@SpringBootApplication
@EnableConfigurationProperties(ProxyProperties.class)
public class ProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProxyApplication.class, args);
    }
}