package com.distributed.articleservice.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.mutable.MutableInt;

import com.distributed.articleservice.entity.ArticleEntity;
import com.distributed.articleservice.entity.ArticleLikesEntity;
import com.distributed.articleservice.exception.ArticleAllreadyExistsException;
import com.distributed.articleservice.exception.ArticleNotFoundException;
import com.distributed.articleservice.model.ArticleModel;

public interface ArticleService {

	boolean saveArticle(ArticleEntity articleEntity) throws ArticleAllreadyExistsException;

	List<ArticleEntity> getArticles();
	
	ArticleEntity updateLikesOnArticle(ArticleModel model);

	ArticleLikesEntity updateLikesOnArticle1(ArticleModel model, String userId);
	
	boolean getLikes(ArticleEntity article, String userId, MutableInt totalLikes);
	
}
