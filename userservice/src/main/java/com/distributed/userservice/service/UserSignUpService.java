package com.distributed.userservice.service;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.distributed.userservice.entity.UserEntity;
import com.distributed.userservice.exception.UserAlreadyExistsException;
import com.distributed.userservice.exception.UserNotFoundException;
import com.distributed.userservice.models.UserModel;
import com.distributed.userservice.repository.UserRepository;
import com.distributed.userservice.util.UserAuthUtil;

@Service
public class UserSignUpService {
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private Environment environment;
	
	private static final Logger logger = LoggerFactory.getLogger(UserSignUpService.class);
	
	/**
	 * Adding user in DB
	 * @param userModel
	 * @return boolean flag
	 * @throws UserAlreadyExistsException
	 * @throws UserNotFoundException
	 */
	
	public boolean addUser(UserModel userModel) throws UserAlreadyExistsException, UserNotFoundException {
		boolean isUserAdded = false;
		if(nonNull(userModel) && StringUtils.isBlank(userModel.getEmail())) {
			logger.info("User fields can not be left blank." );
			throw new UserNotFoundException(environment.getProperty("newsapp.user.cannot.be.null"));
			
		}
		if(nonNull(userModel)) {
			Optional<UserEntity> userOpt = userRepository.findByEmail(userModel.getEmail());
			if(userOpt.isPresent()) {
				logger.info("User already exist" );
				throw new UserAlreadyExistsException(environment.getProperty("newsapp.exiting.user"));
			}
			UserEntity userEntity = UserAuthUtil.convertUserModelToEntity(userModel);
			userEntity.setCreated(LocalDateTime.now());
			UserEntity userEntity_new = userRepository.saveAndFlush(userEntity);
			
			if (nonNull(userEntity_new) && !StringUtils.isBlank(userEntity_new.getEmail())) {
				logger.info("User" + " " +userEntity_new.getFirstName() +" added successfully" );
				isUserAdded = true;
			}
			
		}
		return isUserAdded;
	}
	
	/**
	 * Updating user in DB
	 * @param userModel
	 * @return boolean flag
	 * @throws UserAlreadyExistsException
	 * @throws UserNotFoundException
	 */
	
	public boolean updateUser(UserModel userModel) throws  UserNotFoundException {
		boolean isUserUpdated = false;
		if (isNull(userModel)) {
			throw new UserNotFoundException(environment.getProperty("newsapp.user.cannot.be.null"));
		}
		if (nonNull(userModel)) {
			Optional<UserEntity> userOpt = userRepository.findByEmail(userModel.getEmail());
			if (userOpt.isPresent()) {

				UserEntity userEntity = UserAuthUtil.convertUserModelToEntity(userModel);
				UserEntity userEntity_new = userRepository.saveAndFlush(userEntity);
				if (nonNull(userEntity_new) && !StringUtils.isBlank(userEntity_new.getEmail())) {
					logger.info("User" + " " +userEntity_new.getFirstName() +" updated successfully" );
					isUserUpdated = true;
				}
			}
		}
		return isUserUpdated;
	}
	
	/**
	 * Deleting user from DB
	 * @param userModel
	 * @return boolean flag
	 * @throws UserAlreadyExistsException
	 * @throws UserNotFoundException
	 */
	
	public boolean deleteUser(UserModel userModel) throws  UserNotFoundException {
		boolean isUserDeleted = false;
		if(isNull(userModel)) {
			throw new UserNotFoundException(environment.getProperty("newsapp.user.cannot.be.null"));
		}
		if(nonNull(userModel)) {
			Optional<UserEntity> userOpt = userRepository.findByEmail(userModel.getEmail());
			if(userOpt.isPresent()) {
			
			UserEntity userEntity = UserAuthUtil.convertUserModelToEntity(userModel);
			userRepository.delete(userEntity);
			isUserDeleted= true;
			}
		}
		return isUserDeleted;
	}
	
	/**
	 * Login method which generate JWT token
	 * @param email
	 * @param password
	 * @return token
	 */
	public String login(String email, String password) {
		String token = null;
		if (!StringUtils.isBlank(email) && !StringUtils.isBlank(password)) {
			UserEntity userEntity = userRepository.findByEmailAndPassword(email, password);
			if (nonNull(userEntity)) {
				logger.info("User" +" "+ userEntity.getFirstName() +" login successfully.");
				token = UserAuthUtil.generateToken(userEntity.getEmail());				
			}
			
		}
		return token;
	}

}
