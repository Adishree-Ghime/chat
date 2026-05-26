package com.ai.chat.dto;

public class ChatResponse {
	private String reply;
	
	public ChatResponse () {}
	
	public ChatResponse (String r) {
		this.reply=r;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}
	
	

}
