package com.healthstore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for the Health Store application.
 * This class configures CORS (Cross-Origin Resource Sharing) to allow
 * the frontend application to communicate with the backend API.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    /**
     * Configure CORS mappings to allow frontend applications to access the backend API.
     * This method allows requests from the frontend running on different ports and IP addresses.
     * 
     * @param registry The CORS registry to configure
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:5175",
                "http://172.24.176.1:5175",
                "http://10.175.99.80:5175"
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
