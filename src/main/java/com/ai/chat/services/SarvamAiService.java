package com.ai.chat.services;

import java.util.ArrayList;
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
	
 private final RestTemplate restTemplate = new RestTemplate();
	
 public String askSarvam(List<ChatMessage> history, String userMessage) {
  String url = "https://api.sarvam.ai/v1/chat/completions";
  List<Map<String, String>> messageList = new ArrayList<>();
  
  // Safe System Message addition
  Map<String, String> systemMsg = new HashMap<>();
  systemMsg.put("role", "system");
  systemMsg.put("content", "You are a helpful AI assistant.");
  messageList.add(systemMsg);
  
  // FIXED: Safe loop that doesn't trigger NullPointerException on Map.of()
  if (history != null) {
      for (ChatMessage msg : history) {
          if (msg.getRole() != null && msg.getContent() != null) {
              Map<String, String> msgMap = new HashMap<>();
              msgMap.put("role", msg.getRole());
              msgMap.put("content", msg.getContent());
              messageList.add(msgMap);
          }
      }
  }
  
  // Safe User Message addition
  Map<String, String> userMsgMap = new HashMap<>();
  userMsgMap.put("role", "user");
  userMsgMap.put("content", userMessage != null ? userMessage : "");
  messageList.add(userMsgMap);
  
  Map<String, Object> body = new HashMap<>();
  body.put("model", model != null ? model : "sarvam-2b");
  body.put("messages", messageList);
  body.put("temperature", 0.2);
  body.put("max_tokens", 1000);
  
  HttpHeaders header = new HttpHeaders();
  header.setContentType(MediaType.APPLICATION_JSON);
  
  // Check against empty variables
  if (apiKey != null && !apiKey.isEmpty()) {
      header.setBearerAuth(apiKey);
  } else {
      throw new IllegalStateException("Sarvam API Key is missing or null!");
  }
  
  HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, header);
  
  try {
      ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
      
      if (response.getBody() != null && response.getBody().containsKey("choices")) {
          List choices = (List) response.getBody().get("choices");
          if (choices != null && !choices.isEmpty()) {
              Map firstChoice = (Map) choices.get(0);
              Map messageObj = (Map) firstChoice.get("message");
              return messageObj.get("content").toString();
          }
      }
      return "Error: Empty response body received from AI service.";
  } catch (Exception e) {
      return "AI service error: " + e.getMessage();
  }
 }
}

