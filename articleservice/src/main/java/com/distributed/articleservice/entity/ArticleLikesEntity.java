package com.distributed.articleservice.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ARTICLELIKES", catalog = "NEWSDB")
public class ArticleLikesEntity {
	

	@Id
	@Column(name = "LIKES_ID")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
    @ManyToOne
	@JoinColumn(name="ARTICLE_ID")
	private ArticleEntity article;
	
    @ManyToOne
	@JoinColumn(name="USER_ID")
	private UserEntity user;
	
	public ArticleEntity getArticle() {
		return article;
	}

	public void setArticle(ArticleEntity article) {
		this.article = article;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@Column(name = "Count", unique = true)
	private Integer count;

}
