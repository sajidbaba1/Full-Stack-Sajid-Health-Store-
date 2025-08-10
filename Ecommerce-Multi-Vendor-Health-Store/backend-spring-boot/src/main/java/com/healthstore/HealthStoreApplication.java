package com.healthstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching // Enable Spring's cache abstraction
public class HealthStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(HealthStoreApplication.class, args);
    }
    
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("products", "categories");
    }
}
