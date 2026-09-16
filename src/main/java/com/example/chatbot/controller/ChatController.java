package com.example.chatbot.controller;

import com.example.chatbot.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // POST /api/chat  { "message": "Do you have wireless headphones under $50?" }
    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        String reply = chatService.reply(userMessage);
        return Map.of("reply", reply);
    }
}
