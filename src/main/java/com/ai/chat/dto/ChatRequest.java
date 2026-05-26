package com.ai.chat.dto;

public class ChatRequest {
	private String messages;
	
	public ChatRequest() {}
	public ChatRequest(String ch) {
		this.messages=ch;
	}
	public String getMessage() {
		return messages;
	}
	public void setMessage(String message) {
		this.messages = message;
	}

}
