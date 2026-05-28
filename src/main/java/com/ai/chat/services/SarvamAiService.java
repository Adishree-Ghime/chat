package com.ai.chat.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ai.chat.models.ChatMessage;

@Service
public class SarvamAiService {

    @Value("${sarvam.api.key}")
    private String apiKey;

    @Value("${sarvam.model}")
    private String model;

    private final RestTemplate restTemplate;

    public SarvamAiService() {
        this.restTemplate = new RestTemplate();
    }

    @SuppressWarnings("unchecked")
    public String askSarvam(List<ChatMessage> history, String userMessage) {
        try {
            // 1. Build the Gemini API Endpoint URL
            String targetUrl = "https://generativelanguage.googleapis.com/v1beta/models/" 
                         + this.model + ":generateContent?key=" + this.apiKey;

            // 2. Build the "contents" array payload
            List<Map<String, Object>> contentsList = new ArrayList<>();

            // Process database chat history rows safely
            if (history != null) {
                for (ChatMessage msg : history) {
                    // DEFENSIVE FIX: Skip any corrupted rows where text content is blank or null
                    if (msg.getContent() == null || msg.getContent().trim().isEmpty()) {
                        continue; 
                    }

                    Map<String, Object> contentBlock = new HashMap<>();
                    String role = "assistant".equalsIgnoreCase(msg.getRole()) ? "model" : "user";
                    contentBlock.put("role", role);
                    
                    Map<String, String> textPart = new HashMap<>();
                    textPart.put("text", msg.getContent());
                    contentBlock.put("parts", Collections.singletonList(textPart));
                    
                    contentsList.add(contentBlock);
                }
            }

            // Append the fresh incoming user query (Only if it contains valid text data)
            if (userMessage != null && !userMessage.trim().isEmpty()) {
                Map<String, Object> currentUserBlock = new HashMap<>();
                currentUserBlock.put("role", "user");
                Map<String, String> currentTextPart = new HashMap<>();
                currentTextPart.put("text", userMessage);
                currentUserBlock.put("parts", Collections.singletonList(currentTextPart));
                contentsList.add(currentUserBlock);
            }

            // 3. Assemble Root Payload map
            Map<String, Object> rootPayload = new HashMap<>();
            rootPayload.put("contents", contentsList);

            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("maxOutputTokens", 1000); 
            rootPayload.put("generationConfig", generationConfig);

            // 4. Configure HTTP Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(rootPayload, headers);

            // 5. Post to Gemini Endpoint
            ResponseEntity<Map> response = restTemplate.postForEntity(targetUrl, entity, Map.class);

            // 6. Navigate response map tree structural keys cleanly
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
                
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> firstCandidate = candidates.get(0);
                    Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
                    
                    if (content != null) {
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                        if (parts != null && !parts.isEmpty()) {
                            return (String) parts.get(0).get("text");
                        }
                    }
                }
            }
            
            return "Error: Empty response body received from Gemini API.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling Gemini Backend: " + e.getMessage();
        }
    }
}