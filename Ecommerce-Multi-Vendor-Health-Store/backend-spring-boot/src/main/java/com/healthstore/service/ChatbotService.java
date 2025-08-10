package com.healthstore.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ChatbotService {

    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    public ChatbotService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("").build();
    }

    public String getChatbotResponse(String userMessage) {
        Map<String, Object> requestBody = Map.of(
            "contents", Collections.singletonList(
                Map.of("parts", Collections.singletonList(
                    Map.of("text", userMessage)
                ))
            )
        );

        return webClient.post()
            .uri(geminiApiUrl)
            .body(BodyInserters.fromValue(requestBody))
            .retrieve()
            .bodyToMono(Map.class)
            .map(response -> {
                // This is a basic way to parse the response. We will refine it later.
                Map<String, Object> candidate = (Map<String, Object>) ((List<Object>) response.get("candidates")).get(0);
                Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                return (String) ((Map<String, Object>) ((List<Object>) content.get("parts")).get(0)).get("text");
            })
            .block();
    }
}
