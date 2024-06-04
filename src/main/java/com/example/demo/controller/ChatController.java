package com.example.demo.controller;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

	public static final String PROMPT = """
			Hello""";

	private final ChatClient aiClient;

	// Using an autoconfigured ChatClient.Builder
	public ChatController(ChatClient.Builder chatClientBuilder) {
        this.aiClient = chatClientBuilder.build();
    }

	@GetMapping("/test")
	public ResponseEntity<String> generateAdvice() {
		return ResponseEntity.ok(aiClient.call(PROMPT));
	}

}