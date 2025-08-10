package com.healthstore.controller;

import com.healthstore.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/ask")
    public ResponseEntity<String> askChatbot(@RequestBody String userMessage) {
        String botResponse = chatbotService.getChatbotResponse(userMessage);
        return ResponseEntity.ok(botResponse);
    }
}
