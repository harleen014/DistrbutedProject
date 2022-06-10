package com.distributed.articleservice.controller;

import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.distributed.articleservice.entity.ArticleEntity;
import com.distributed.articleservice.entity.ArticleLikesEntity;
import com.distributed.articleservice.exception.ArticleAllreadyExistsException;
import com.distributed.articleservice.exception.ArticleNotFoundException;
import com.distributed.articleservice.model.ArticleModel;
import com.distributed.articleservice.service.ArticleService;
import com.distributed.articleservice.util.ArticleServiceUtil;

import io.jsonwebtoken.Jwts;

@RestController
@CrossOrigin(origins = { "http://localhost:4200" }, maxAge = 4800)
@RequestMapping("/news/api/v1")
public class ArticleController {

	@Autowired
	private ArticleService articleService;
	@Autowired
	private Environment env;

	/**
	 * This method is used to save article
	 * for the current logged in user.
	 * @param model
	 * @param request
	 * @param response
	 * @return boolean
	 */

	@PostMapping(value = "/article", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> saveArticle(@RequestBody ArticleModel model, HttpServletRequest request,
			HttpServletResponse response) {

		String token = getToken(request);
		String userId = getUserIdFromToken(token);
		if (nonNull(model)) {
			try {
				model.setUserId(userId);
				model.setCountlike(0);
				articleService.saveArticle(ArticleServiceUtil.convertArticleModelToEntity(model));
			} catch (ArticleAllreadyExistsException ex) {
				return new ResponseEntity<String>(ex.getMessage(), HttpStatus.CONFLICT);
			}
		}

		return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
	}

	/**
	 * This method is used to fetch saved articles
	 * for the current logged in user.
	 * @param request
	 * @return List of articles
	 */

	@GetMapping(value = "/articles", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getArticles(HttpServletRequest request) {

			String token = getToken(request);
			String userId = getUserIdFromToken(token);
			List<ArticleEntity> articleList = articleService.getArticles();
			
			List<ArticleModel> articleModelList = new ArrayList<>();
			for (ArticleEntity article : articleList) {
				MutableInt totalLikes = new MutableInt(0);
				ArticleModel model = ArticleServiceUtil.convertArticleEntityToModel(article);
				boolean liked = articleService.getLikes(article, userId, totalLikes);
				model.setUserliked(liked);
				model.setTotalLikes(totalLikes.intValue());
				articleModelList.add(model);
				
				
			}

			return new ResponseEntity<List<ArticleModel>>(articleModelList, HttpStatus.OK);
//			
//			if (!CollectionUtils.isEmpty(articleList)) {
//				articleModelList = articleList.stream()
//						.map(article -> FavouriteServiceUtil.convertArticleEntityToModel(article))
//						.collect(Collectors.toList());
//				return new ResponseEntity<List<ArticleModel>>(articleModelList, HttpStatus.OK);
//			}
//		//}
//		return new ResponseEntity<Boolean>(Boolean.FALSE, HttpStatus.NO_CONTENT);
	}
	
	@PostMapping(value = "/articleLikes", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateArticleLikes(HttpServletRequest request, @RequestBody ArticleModel model) {

		String token = getToken(request);
		String userId = getUserIdFromToken(token);
		//if (!StringUtils.isBlank(userId)) {
			
		ArticleLikesEntity  likes = articleService.updateLikesOnArticle1(model, userId);
		ArticleModel updatedModel = ArticleServiceUtil.convertArticleEntityToModel(likes.getArticle());
		updatedModel.setUserliked(true);
		
		MutableInt totalLikes = new MutableInt(0);
		boolean liked = articleService.getLikes(likes.getArticle(), userId, totalLikes);
		updatedModel.setTotalLikes(totalLikes.intValue());
		
			
		return new ResponseEntity<ArticleModel>(updatedModel, HttpStatus.OK);
	}

	
	/**
	 * Fetch JWT token from request.
	 * @param request
	 * @return token 
	 */
	private String getToken(HttpServletRequest request) {
		final String authHeader = request.getHeader("Authorization");
		final String token = authHeader.substring(7);
		return token;
	}

	/**
	 * Fetch userId from JWT token.
	 * @param token
	 * @return user Id
	 */
	private String getUserIdFromToken(String token) {
		String userId = Jwts.parser().setSigningKey(env.getProperty("newsapp.jwt.secret.key")).parseClaimsJws(token)
				.getBody().getSubject();
		return userId;
	}
}
