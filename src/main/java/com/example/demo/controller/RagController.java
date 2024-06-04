package com.example.demo.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RagController {
	
	private final ChatClient aiClient;
	private final VectorStore vectorStore;

	// Using an autoconfigured ChatClient.Builder
	public RagController(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.aiClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }
	
	@GetMapping("/test/rag")
	public ResponseEntity<String> testRag() {
		return ResponseEntity.ok("Test is ok !");
	}
	
	// Returns the response if known from the document, or else return unknown
	@GetMapping("/rag")
	public ResponseEntity<String> generateAnswer(@RequestParam String query) {
		// Searches for similar text using the embeddings stored in vectorstore
		List<Document> similarDocs = vectorStore.similaritySearch(query);
		String information = similarDocs.stream().map(Document::getContent).collect(Collectors.joining(System.lineSeparator()));
		var systemPromptTemplate = new SystemPromptTemplate(
                """
                            You are a helpful assistant.
                            Use only the following information to answer the question.
                            Do not use any other information. If you do not know, simply answer: Unknown.

                            {information}
                        """);
		var systemMessage = systemPromptTemplate.createMessage(Map.of("information", information));
		var userPromptTemplate = new PromptTemplate("{query}");
		var userMessage = userPromptTemplate.createMessage(Map.of("query", query));
		var prompt = new Prompt(List.of(systemMessage, userMessage));
		return ResponseEntity.ok(aiClient.call(prompt).getResult().getOutput().getContent());

	}

}
