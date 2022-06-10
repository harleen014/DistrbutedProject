package com.distributed.articleservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.distributed.articleservice.entity.ArticleEntity;
import com.distributed.articleservice.entity.ArticleLikesEntity;

public interface ArticleLikesRepository extends JpaRepository<ArticleLikesEntity, Integer> {
	
	List<ArticleLikesEntity> findAll();
//	ArticleLikesEntity  findbyArticle(ArticleEntity articleID);
//	ArticleLikesEntity findbyUserId (Integer UserId);

}
