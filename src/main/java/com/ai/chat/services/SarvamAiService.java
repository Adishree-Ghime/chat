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
	   
	   public String askSarvam(List<ChatMessage> history, String userMessage ) {

		    String url = "https://generativelanguage.googleapis.com/v1beta/models";

		    List<Map<String, String>> messages = new ArrayList<>();

		    Map<String, String> systemMsg = new HashMap<>();
		    systemMsg.put("role", "system");
		    systemMsg.put("content", "You are a helpful AI assistant.");
		    messages.add(systemMsg);

//		    for (ChatMessage msg : history) {
//
//		        if (msg == null) continue;
//
//		        Map<String, String> map = new HashMap<>();
//
//		        map.put("role",
//		                msg.getRole() != null ? msg.getRole() : "user");
//
//		        map.put("content",
//		                msg.getContent() != null ? msg.getContent() : "");
//
//		        messages.add(map);
//		    }
		    for (ChatMessage msg : history) {

		        if (msg == null) {
		            continue;
		        }

		        if (msg.getContent() == null ||
		            msg.getContent().trim().isEmpty()) {

		            continue;
		        }

		        Map<String, String> map = new HashMap<>();

		        map.put("role",
		                msg.getRole() != null ? msg.getRole() : "user");

		        map.put("content", msg.getContent().trim());

		        messages.add(map);
		    }

		    Map<String, String> userMap = new HashMap<>();
		    userMap.put("role", "user");
//		    userMap.put("content",
//		            userMessage != null ? userMessage : "");
		    if (userMessage == null || userMessage.trim().isEmpty()) {
		        throw new IllegalArgumentException(
		            "User message cannot be empty");
		    }

		    userMap.put("content", userMessage.trim());

		    messages.add(userMap);

		    Map<String, Object> body = new HashMap<>();
		    body.put("model", model);
		    body.put("messages", messages);
		    body.put("temperature", 0.2);
		    body.put("max_tokens", 1000);

		    HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);
		    headers.setBearerAuth(apiKey);

		    HttpEntity<Map<String, Object>> entity =
		            new HttpEntity<>(body, headers);

		    ResponseEntity<Map> response =
		            restTemplate.postForEntity(url, entity, Map.class);

		    List choices = (List) response.getBody().get("choices");

		    Map firstChoice = (Map) choices.get(0);

		    Map message1 = (Map) firstChoice.get("message");

		    return (String) message1.get("content");
		}
	   
//	   public String askSarvam(List<ChatMessage> history, String userMessage ) {
//	       String url = "https://api.sarvam.ai/v1/chat/completions";
//	       List<Map<String, String>> message = new ArrayList<>();
//	       
//	       message.add(Map.of(
//	               "role","system","content","You are a help AI assistance."));
//	       
//	       for (ChatMessage msg: history ) {
//	           message.add(Map.of(
//	                   "role",msg.getRole(),
//	                   "content",msg.getContent()));
//	       }
//	       
//	       message.add(Map.of(
//	               "role","user",
//	               "content", userMessage));
//	       
//	       Map<String, Object> body = new HashMap<>();
//	       body.put("model", model);
//	       body.put("messages", message);
//	       body.put("temperature", 0.2);
//	       body.put("max_tokens", 1000);
//	       
//	       HttpHeaders header = new HttpHeaders();
//	       header.setContentType(MediaType.APPLICATION_JSON);
//	       
//	       header.setBearerAuth(apiKey);
//	       HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, header);
//	       
//	       ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
//	       
//	       List choice = (List) response.getBody().get("choices");
//	       
//	       Map firstChoice = (Map) choice.get(0);
//	       Map message1 = (Map) firstChoice.get("message");
//	       
//	       return (String) message1.get("content");
//	   }
//
// @Value("${sarvam.api.key}")
// private String apiKey;
//	
// @Value("${sarvam.model}")
// private String model;
//	
// private final RestTemplate restTemplate = new RestTemplate();
//	
// public String askSarvam(List<ChatMessage> history, String userMessage) {
//  String url = "https://api.sarvam.ai/v1/chat/completions";
//  List<Map<String, String>> messageList = new ArrayList<>();
//  
//  // Safe System Message addition
//  Map<String, String> systemMsg = new HashMap<>();
//  systemMsg.put("role", "system");
//  systemMsg.put("content", "You are a helpful AI assistant.");
//  messageList.add(systemMsg);
//  
//  // FIXED: Safe loop that doesn't trigger NullPointerException on Map.of()
//  if (history != null) {
//      for (ChatMessage msg : history) {
//          if (msg.getRole() != null && msg.getContent() != null) {
//              Map<String, String> msgMap = new HashMap<>();
//              msgMap.put("role", msg.getRole());
//              msgMap.put("content", msg.getContent());
//              messageList.add(msgMap);
//          }
//      }
//  }
//  
//  // Safe User Message addition
//  Map<String, String> userMsgMap = new HashMap<>();
//  userMsgMap.put("role", "user");
//  userMsgMap.put("content", userMessage != null ? userMessage : "");
//  messageList.add(userMsgMap);
//  
//  Map<String, Object> body = new HashMap<>();
//  body.put("model", model != null ? model : "sarvam-2b");
//  body.put("messages", messageList);
//  body.put("temperature", 0.2);
//  body.put("max_tokens", 1000);
//  
//  HttpHeaders header = new HttpHeaders();
//  header.setContentType(MediaType.APPLICATION_JSON);
//  
//  // Check against empty variables
//  if (apiKey != null && !apiKey.isEmpty()) {
//      header.setBearerAuth(apiKey);
//  } else {
//      throw new IllegalStateException("Sarvam API Key is missing or null!");
//  }
//  
//  HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, header);
//  
//  try {
//      ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
//      
//      if (response.getBody() != null && response.getBody().containsKey("choices")) {
//          List choices = (List) response.getBody().get("choices");
//          if (choices != null && !choices.isEmpty()) {
//              Map firstChoice = (Map) choices.get(0);
//              Map messageObj = (Map) firstChoice.get("message");
//              return messageObj.get("content").toString();
//          }
//      }
//      return "Error: Empty response body received from AI service.";
//  } catch (Exception e) {
//      return "AI service error: " + e.getMessage();
//  }
// }
}

