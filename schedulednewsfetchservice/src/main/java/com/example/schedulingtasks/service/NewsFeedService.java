package com.example.schedulingtasks.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.example.schedulingtasks.entity.ArticleEntity;
import com.example.schedulingtasks.exception.ArticleAllreadyExistsException;



public interface NewsFeedService {

	boolean saveArticle(ArticleEntity articleEntity) throws ArticleAllreadyExistsException;

	//List<ArticleEntity> deleteArticle(String author,String publishedAt) throws ArticleNotFoundException;

}
