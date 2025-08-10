package com.healthstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Health Store e-commerce platform.
 * This class bootstraps the Spring Boot application, enabling auto-configuration
 * and component scanning for the entire application.
 */
@SpringBootApplication
public class HealthStoreApplication {

    /**
     * Entry point of the application.
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(HealthStoreApplication.class, args);
    }
}