package com.distributed.articleservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static java.util.Objects.isNull;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.distributed.articleservice.entity.ArticleEntity;
import com.distributed.articleservice.entity.ArticleLikesEntity;
import com.distributed.articleservice.entity.UserEntity;
import com.distributed.articleservice.exception.ArticleAllreadyExistsException;
import com.distributed.articleservice.exception.ArticleNotFoundException;
import com.distributed.articleservice.model.ArticleModel;
import com.distributed.articleservice.repository.ArticleLikesRepository;
import com.distributed.articleservice.repository.ArticleRepository;
import com.distributed.articleservice.repository.UserRepository;

@Service
public class ArticleServiceImpl implements ArticleService {


	 private static final Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);
	 
	@Autowired
	ArticleRepository articleRepository;
	@Autowired
	ArticleLikesRepository articleLikesRepository;
	@Autowired
	UserRepository userRepository;
	

	@Override
	public List<ArticleEntity> getArticles() {
		logger.info("Calling getArticles() to get all the articles from Article table ..........................." );
		 List<ArticleEntity> articles = null;
		  //if(!StringUtils.isBlank(userId)) {
			  articles = articleRepository.findAll();
		  //}
		return articles;
	}
	
	@Override
	public boolean getLikes(ArticleEntity article, String userId, MutableInt totalLikes)
	{
		List<ArticleLikesEntity> likes = articleLikesRepository.findAll();

		for(ArticleLikesEntity like : likes){
			Integer aId = like.getArticle().getId();
			if(aId == article.getId()) {
				totalLikes.increment();
			}
		}
		for(ArticleLikesEntity like : likes){
			Integer aId = like.getArticle().getId();
			boolean val = (aId == article.getId() && like.getUser().getEmail().equalsIgnoreCase(userId));
			if (val)
				return true;
		}
		
		return false;
	}
	
	@Override
	public ArticleEntity updateLikesOnArticle(ArticleModel model) {
		 ArticleEntity article = null;
		 article = articleRepository.findByAuthorAndPublishedAt(model.getAuthor(), model.getPublishedAt());
		 System.out.println("Before increasing count");
		 if(article!= null) {
			 article.setCountlike(article.getCountlike()+1);
			 System.out.println(article.getAuthor()+ "    " + article.getCountlike());
			 articleRepository.save(article);
			 return article;
		 }
		 else return null;
		 
	}
	
	@Override
	public boolean saveArticle(ArticleEntity articleEntity) throws ArticleAllreadyExistsException {
		boolean isSaved = false;
		if(isNull(articleRepository.findByAuthorAndPublishedAt(articleEntity.getAuthor(), articleEntity.getPublishedAt()))) {
			articleRepository.saveAndFlush(articleEntity);
			isSaved =true;
		}
		else {
			throw new ArticleAllreadyExistsException("This Aricle  has been already added for the current user.");
		}
		
		return isSaved;
	}

	@Override
	public ArticleLikesEntity updateLikesOnArticle1(ArticleModel model, String userId) {
		ArticleEntity article = null;
		 article = articleRepository.findByAuthorAndPublishedAt(model.getAuthor(), model.getPublishedAt());
		 UserEntity user = userRepository.findByEmail(userId);
		 System.out.println("Before increasing count");
		 if(article!= null && user != null) {
			 ArticleLikesEntity likes = new ArticleLikesEntity();
			 likes.setArticle(article);
			 likes.setUser(user);
			 logger.info("Updating likes for given article id by the user in the articleLikes table");
			 articleLikesRepository.save(likes);
			 return likes;
		 }
		 else return null;
		 
	}

}
