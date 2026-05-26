package com.ai.chat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


import com.ai.chat.models.AppUser;

public interface User_repo extends JpaRepository<AppUser, Long> {
    
	Optional<AppUser> findByUsername(String string);
}
