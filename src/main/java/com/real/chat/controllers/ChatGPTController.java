package com.real.chat.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@RestController
public class ChatGPTController {
    private final WebClient webClient;

    @Value("${chatgpt.api.key}")
    private String apiKey;

    public ChatGPTController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
    }

    @GetMapping("/chatgpt/message")
    public Mono<String> sendMessageToChatGPT(@RequestParam String message) {
        return webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{ \"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"user\", \"content\": \""
                        + message + "\"}] }")
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> "Response from ChatGPT: " + response);
    }
}