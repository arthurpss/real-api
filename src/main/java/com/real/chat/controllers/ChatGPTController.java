package com.real.chat.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.real.chat.dtos.ChatGPTResponse;
import com.real.chat.exceptions.CustomException;

import reactor.core.publisher.Mono;

@RestController
public class ChatGPTController {
  private final WebClient webClient;

  @Value("${chatgpt.api.key}")
  private String apiKey;

  public ChatGPTController(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
  }

  @GetMapping("api/chatgpt/message")
  public Mono<String> sendMessageToChatGPT(@RequestParam String message) {
    String model = "gpt-4o-mini";
    String assistantRole = "assistant";
    String assistantContent = "Você é um assistente de uma loja de piscinas. Deverá tirar dúvidas sobre produtos e serviços dessa loja.";
    String userRole = "user";
    String userContent = message;

    String jsonBody = String.format("""
        {
            "model": "%s",
            "messages": [
              {
                "role": "%s",
                "content": "%s"
              },
              {
                "role": "%s",
                "content": "%s"
              }
            ]
        }""", model, assistantRole, assistantContent, userRole, userContent);

    return webClient.post()
        .uri("/chat/completions")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer " + apiKey)
        .bodyValue(jsonBody)
        .retrieve()
        .bodyToMono(ChatGPTResponse.class)
        .map(response -> {
          if (response.choices.isEmpty()) {
            return "Desculpe, não entendi o que você quis dizer.";
          }
          return response.choices.get(0).message.content;
        })
        .onErrorMap(WebClientResponseException.class,
            ex -> new CustomException("API request failed with status: " + ex.getStatusCode()))
        .onErrorResume(e -> {
          System.err.println("Error occurred: " + e.getMessage());
          return Mono.just("An error occurred, please try again later.");
        });

  }
}