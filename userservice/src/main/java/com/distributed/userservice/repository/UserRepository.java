package com.distributed.userservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.distributed.userservice.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
	
	public Optional<UserEntity> findByEmail(String email);
	
	public UserEntity findByEmailAndPassword(String email,String password);

}
