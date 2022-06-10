package com.example.schedulingtasks.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static java.util.Objects.isNull;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.schedulingtasks.entity.ArticleEntity;
import com.example.schedulingtasks.exception.ArticleAllreadyExistsException;
import com.example.schedulingtasks.exception.ArticleNotFoundException;
import com.example.schedulingtasks.repository.NewsFeedRepository;


@Service
public class NewsFeedServiceImpl implements NewsFeedService {


	 private static final Logger logger = LoggerFactory.getLogger(NewsFeedServiceImpl.class);
	 
	@Autowired
	NewsFeedRepository newsFeedRepository;
	

	
	
	@Override
	public boolean saveArticle(ArticleEntity articleEntity) throws ArticleAllreadyExistsException {
		boolean isSaved = false;
		if(isNull(newsFeedRepository.findByAuthorAndPublishedAt(articleEntity.getAuthor(), articleEntity.getPublishedAt()))) {
			newsFeedRepository.saveAndFlush(articleEntity);
			isSaved =true;
		}
		else {
			throw new ArticleAllreadyExistsException("This Aricle  has been already added");
		}
		
		return isSaved;
	}

}
