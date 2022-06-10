package com.distributed.articleservice.util;

import static java.util.Objects.nonNull;

import org.springframework.beans.BeanUtils;

import com.distributed.articleservice.entity.ArticleEntity;
import com.distributed.articleservice.entity.SourceEntity;
import com.distributed.articleservice.model.ArticleModel;
import com.distributed.articleservice.model.SourceModel;

public class ArticleServiceUtil {

	/**
	 * Utility method to convert from model to entity.
	 * 
	 * @param articleModel
	 * @return ArticleEntity
	 */
	public static ArticleEntity convertArticleModelToEntity(ArticleModel articleModel) {

		ArticleEntity articleEntity = null;
		if (nonNull(articleModel)) {
			articleEntity = new ArticleEntity();
			SourceModel sourcemodel = articleModel.getSourceModel();
			BeanUtils.copyProperties(articleModel, articleEntity);
			articleEntity.setContent(substringValue(articleEntity.getContent()));
			articleEntity.setDescription(substringValue(articleEntity.getDescription()));
			articleEntity.setSourceEntity(convertSourceModelToEntity(sourcemodel));
		}
		return articleEntity;
	}

	/**
	 * Utility method to convert from model to entity.
	 * 
	 * @param sourceModel
	 * @return SourceEntity
	 */
	public static SourceEntity convertSourceModelToEntity(SourceModel sourceModel) {

		SourceEntity sourceEntity = null;
		if (nonNull(sourceModel)) {
			sourceEntity = new SourceEntity();
			sourceEntity.setSourceId(sourceModel.getSourceId());
			sourceEntity.setSourceName(sourceModel.getSourceName());
		}
		return sourceEntity;
	}
	

	/**
	 * Utility method to convert from model to entity.
	 * 
	 * @param articleEntity
	 * @return ArticleModel
	 */
	public static ArticleModel convertArticleEntityToModel(ArticleEntity articleEntity) {
		
		ArticleModel articleModel = null;
		SourceModel sourceModel = null;
		if (nonNull(articleEntity)) {
			articleModel = new ArticleModel();
			if(nonNull(articleEntity.getSourceEntity())) {
				sourceModel = convertSourceEntityToModel(articleEntity.getSourceEntity());
			}
			BeanUtils.copyProperties(articleEntity, articleModel);
			articleModel.setSourceModel(sourceModel);
			
		}
		return articleModel;
	}

	/**
	 * 
	 * @param sourceEntity
	 * @return
	 */
	private static SourceModel convertSourceEntityToModel(SourceEntity sourceEntity) {
		SourceModel source = null;
		if (nonNull(sourceEntity)) {
			source = new SourceModel();
			source.setSourceId(sourceEntity.getSourceId());
			source.setSourceName(sourceEntity.getSourceName());
		}
		return source;
	}
	
	/**
	 * 
	 * @param modelValue
	 * @return
	 */
	private static String substringValue(String modelValue) {
	   String value = null;
	   if(nonNull(modelValue) && modelValue.length()>100) {
		   value = modelValue.substring(0, 100);
	   }
	   return value;
	}

}
