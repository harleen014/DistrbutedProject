package com.example.schedulingtasks.controller;

import static java.util.Objects.nonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.json.JSONParser;
import org.json.simple.*;
import org.json.simple.parser.ParseException;
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

import com.example.schedulingtasks.exception.ArticleAllreadyExistsException;
import com.example.schedulingtasks.exception.ArticleNotFoundException;
import com.example.schedulingtasks.model.ArticleModel;
import com.example.schedulingtasks.service.NewsFeedService;
import com.example.schedulingtasks.utils.SchedulingTasksUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.jsonwebtoken.Jwts;

@RestController
//@CrossOrigin(origins = { "http://localhost:4200" }, maxAge = 4800)
@RequestMapping("/news/api/v1")
public class NewsFeedController {

	@Autowired
	private NewsFeedService articleService;
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

	//@PostMapping(value = "/article", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> saveArticle(@RequestBody ArticleModel model, HttpServletRequest request,
			HttpServletResponse response) {

		//String token = getToken(request);
		//String userId = getUserIdFromToken(token);
		if (nonNull(model)) {
			try {
				//model.setUserId(userId);
				//model.setCountlike(0);
				articleService.saveArticle(SchedulingTasksUtil.convertArticleModelToEntity(model));
			} catch (ArticleAllreadyExistsException ex) {
				return new ResponseEntity<String>(ex.getMessage(), HttpStatus.CONFLICT);
			}
		}

		return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
	}
	
	@RequestMapping("/getNews")
	public String getNews() throws IOException {
		String newsapiURL= "https://newsapi.org/v2/everything?";
		String newsApiKey= "23fc747ac3e947c5a976c5b1dc628281";
		LocalDate date = java.time.LocalDate.now();
		String finalURL = newsapiURL + "q=headlines" + "&from=" + date + "&sortBy=publishedAt&" + "apiKey=" + newsApiKey;
		URL url;
		String jsonText;
	    HttpURLConnection httpURLConnection = null;
	    try
	    {
	        url = new URL(finalURL);
	        httpURLConnection = (HttpURLConnection) url.openConnection();
	        httpURLConnection.setRequestMethod("GET");

	        InputStream inputStream;
	        if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK)

	            inputStream = httpURLConnection.getErrorStream();
	        else
	            inputStream = httpURLConnection.getInputStream();

	        
	        //BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	        BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
	        jsonText = readAll(rd);
	        ObjectMapper mapper = new ObjectMapper();
	        //mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

	        JsonObject jsonObject = new JsonParser().parse(jsonText).getAsJsonObject();
	        JsonElement artObj = jsonObject.get("articles");

	        Gson gson = new Gson();
	        ArticleModel[] articles = gson.fromJson(artObj.toString(), ArticleModel[].class);
	        
	        
	        for(ArticleModel article: articles){
	        	saveArticle(article, null, null);
	        }
	        
	        System.out.println(articles.toString());
//	        JSONParser parser = new JSONParser(jsonText);
//	        JSONObject obj;
//	        try {
//	           obj = (JSONObject)parser.parse();
//	        } catch(Exception e) {
//	           e.printStackTrace();
//	        }
	        
	        
	        //ArticleModel articleModel = gson.fromJson(obj["articles"].toString(), ArticleModel.class);


	    }
	    catch (Exception e)
	    {
	     e.printStackTrace();
	     return  null;
	    }
	    finally {
	        if(httpURLConnection != null)
	        {
	            httpURLConnection = null;
	        }
	    }
		return jsonText;
	}
	
	
	private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
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
