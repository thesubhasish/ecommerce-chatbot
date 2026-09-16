package com.example.chatbot.service;

import com.example.chatbot.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final RestTemplate restTemplate;
    private final ProductSearchService productSearchService;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.chat.model}")
    private String chatModel;

    private static final String CHAT_URL = "https://api.openai.com/v1/chat/completions";

    public ChatService(RestTemplate restTemplate, ProductSearchService productSearchService) {
        this.restTemplate = restTemplate;
        this.productSearchService = productSearchService;
    }

    @SuppressWarnings("unchecked")
    public String reply(String userMessage) {
        // 1. Semantic search: find the products most relevant to the question
        List<Product> matches = productSearchService.search(userMessage, 3);
        String context = matches.stream()
                .map(Product::toContextText)
                .collect(Collectors.joining("\n"));

        // 2. Build a small prompt: system instructions + retrieved context + user question
        String systemPrompt = "You are a helpful ecommerce customer support assistant. "
                + "Answer the customer using ONLY the product information below. "
                + "If the answer isn't in the product info, say you don't have that information.\n\n"
                + "Product info:\n" + context;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "model", chatModel,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userMessage)
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        Map<String, Object> response = restTemplate.postForObject(CHAT_URL, request, Map.class);

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

        return (String) message.get("content");
    }
}
