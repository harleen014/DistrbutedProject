package com.example.schedulingtasks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.schedulingtasks.entity.ArticleEntity;


public interface NewsFeedRepository extends JpaRepository<ArticleEntity, Integer> {
	
	List<ArticleEntity> findAll();
	ArticleEntity  findByAuthorAndPublishedAt(String author,String publishedAt);
	ArticleEntity findByPublishedAt (String publishedAt);

}
