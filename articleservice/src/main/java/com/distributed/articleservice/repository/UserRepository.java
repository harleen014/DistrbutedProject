package com.distributed.articleservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.distributed.articleservice.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
	
	
	public UserEntity findByEmail(String email);
	
	public UserEntity findByEmailAndPassword(String email,String password);

}

