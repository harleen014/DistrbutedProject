package com.distributed.articleservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.distributed.articleservice.entity.ArticleEntity;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Integer> {
	
	List<ArticleEntity> findAll();
	ArticleEntity  findByAuthorAndPublishedAt(String author,String publishedAt);
	ArticleEntity findByPublishedAt (String publishedAt);

}
