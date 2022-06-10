/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.schedulingtasks;

import static java.util.Objects.nonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.schedulingtasks.exception.ArticleAllreadyExistsException;
import com.example.schedulingtasks.model.ArticleModel;
import com.example.schedulingtasks.service.NewsFeedService;
import com.example.schedulingtasks.utils.SchedulingTasksUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Component
public class ScheduledTasks {

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	@Autowired
	private NewsFeedService newsFeedService;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Scheduled(fixedRate = 3600000)
	public void refreshNewsArticles() {
		// log.info("The time is now {}", dateFormat.format(new Date()));
		getNewsFeed();

	}

	@SuppressWarnings("deprecation")
	public void getNewsFeed() {

		String newsapiURL = "https://newsapi.org/v2/everything?";
		String newsApiKey = "23fc747ac3e947c5a976c5b1dc628281";
		LocalDate date = java.time.LocalDate.now();
		String finalURL = newsapiURL + "q=headlines" + "&from=" + date + "&sortBy=publishedAt&" + "apiKey="
				+ newsApiKey;
		URL url;
		String jsonText;
		HttpURLConnection httpURLConnection = null;
		try {
			url = new URL(finalURL);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("GET");

			InputStream inputStream;
			if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK)

				inputStream = httpURLConnection.getErrorStream();
			else
				inputStream = httpURLConnection.getInputStream();

			BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
			jsonText = readAll(rd);
			// ObjectMapper mapper = new ObjectMapper();
			// mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

			JsonObject jsonObject = new JsonParser().parse(jsonText).getAsJsonObject();
			JsonElement artObj = jsonObject.get("articles");

			Gson gson = new Gson();
			ArticleModel[] articles = gson.fromJson(artObj.toString(), ArticleModel[].class);

			for (ArticleModel article : articles) {
				saveArticle(article, null, null);
			}

			log.info("News Articles refreshed at :" + dateFormat.format(new Date()));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (httpURLConnection != null) {
				httpURLConnection = null;
			}
		}
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public ResponseEntity<?> saveArticle(@RequestBody ArticleModel model, HttpServletRequest request,
			HttpServletResponse response) {

		// String token = getToken(request);
		// String userId = getUserIdFromToken(token);
		if (nonNull(model)) {
			try {
				// model.setUserId(userId);
				// model.setCountlike(0);
				newsFeedService.saveArticle(SchedulingTasksUtil.convertArticleModelToEntity(model));
			} catch (ArticleAllreadyExistsException ex) {
				return new ResponseEntity<String>(ex.getMessage(), HttpStatus.CONFLICT);
			}
		}

		return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
	}
}
