package com.ai.chat.models;



import java.time.LocalDateTime;



import jakarta.persistence.Column;

import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;

import jakarta.persistence.GenerationType;

import jakarta.persistence.Id;

import jakarta.persistence.ManyToOne;



@Entity

public class ChatMessage {

	@Id

	@GeneratedValue(strategy=GenerationType.IDENTITY)

	private Long chat_id;

	private String role;

	@Column(columnDefinition="TEXT")

	private String content;

	private LocalDateTime createdAt = LocalDateTime.now();

	

@ManyToOne

private AppUser user;



public Long getChat_id() {

	return chat_id;

}

public void setChat_id(Long chat_id) {

	this.chat_id = chat_id;

}

public String getRole() {

	return role;

}

public void setRole(String role) {

	this.role = role;

}

public String getContent() {

	return content;

}

public void setContent(String content) {

	this.content = content;

}

public LocalDateTime getCreatedAt() {

	return createdAt;

}

public void setCreatedAt(LocalDateTime createdAt) {

	this.createdAt = createdAt;

}

public AppUser getUser() {

	return user;

}

public void setUser(AppUser user) {

	this.user = user;

}

public ChatMessage() {}

public ChatMessage(Long i,String r,String t,AppUser au) {

	this.chat_id=i;

	this.content=t;

	this.role=r;

	this.user=au;

	}

}




