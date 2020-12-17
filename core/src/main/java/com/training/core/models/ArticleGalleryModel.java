package com.training.core.models;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.training.core.constants.Constants;
import com.training.core.helper.TrainingHelper;
import com.training.core.pojos.ArticlePojo;
import com.training.core.services.TileGalleryAndLandingService;

/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ArticleGalleryModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(ArticleGalleryModel.class);
	@Self
	Resource resource;
	
	@Inject
	private TileGalleryAndLandingService tileGalleryAndLandingService;
	
	private List<ArticlePojo> categoryArticleList = new LinkedList<>();
	private List<ArticlePojo> byDateArticleList = new LinkedList<>();
	private List<ArticlePojo> manualAuthorArticleList = new LinkedList<>();
	String homePagePath;
	ResourceResolver resolver;
	@Inject
	private String galleryCategory;
	@Inject
	String[] pages;
	
	/**
	 * The init method. which will fetch the list of articles
	 * 
	 */
	@PostConstruct
	protected void init() {
		LOGGER.info("ArticleGallery Model init Start");
		try {
			if (resource != null) {
				resolver = resource.getResourceResolver();
				homePagePath=TrainingHelper.getHomePagePath(resource);
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured", e);
		}
		LOGGER.info("ArticleGallery Model init End");
	}

	

	/**
	 * method returns characters specific to category
	 * 
	 * @return categoryCharacterList
	 */
	public List<ArticlePojo> getCategoryArticleList() {
		LOGGER.info("GetCategoryArticleList -> Start");
		TagManager tagManager = resource.getResourceResolver().adaptTo(TagManager.class);
		if (galleryCategory != null && tagManager != null) {
			Tag galleryTag = tagManager.resolve(galleryCategory);
			String galleryTagId = galleryTag.getTagID();
			categoryArticleList = tileGalleryAndLandingService.getArticleTilesByDate(homePagePath, 
					 galleryTagId, resource.getResourceResolver());
		}
		LOGGER.info("GetCategoryCharacterList -> End");
		return categoryArticleList;
	}

	/**
	 * method returns characters specific to category
	 * 
	 * @return categoryCharacterList
	 */
	public List<ArticlePojo> getByDateArticleList() {
		LOGGER.info("GetByDateArticleList -> Start");
		if (homePagePath!= null) {
			byDateArticleList = tileGalleryAndLandingService.getArticleTilesByDate(homePagePath, 
					 null, resource.getResourceResolver());
		}
		LOGGER.info("GetByDateArticleList -> End");
		return byDateArticleList;
	}
	
	
	/**
	 * method to fetch the Articles in manual mode
	 * 
	 * @return manualAuthorArticleList
	 * @throws RepositoryException 
	 */
	public List<ArticlePojo> getManualAuthorArticleList() throws RepositoryException {
		LOGGER.info("getManualAuthorArticleList -> Start");
		try {
		for(String articlePage : pages)
		{
			if(articlePage!=null)
			{
			String articlePagePath = articlePage+Constants.JCR_ARTICLE_NODE;
			LOGGER.info("getManualAuthorArticleList for articlePage>>> {}",articlePage);
			ArticlePojo articlePojo = tileGalleryAndLandingService.getManualAuthorArticleList(articlePagePath);
			manualAuthorArticleList.add(articlePojo);
			
			}
		}
		}catch (RepositoryException e) {
			LOGGER.error("RepositoryException Occured ", e);
		}
		LOGGER.info("getManualAuthorCharList -> End");
		return manualAuthorArticleList;
	}
	
	public void setHomePagePath(String homePagePath) {
		this.homePagePath = homePagePath;
	}
	
	public void setTileGalleryAndLandingService(TileGalleryAndLandingService tileGalleryAndLandingService) {
		this.tileGalleryAndLandingService = tileGalleryAndLandingService;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
	public void setPages(String[] pages) {
		this.pages = pages;
	}

	public void setGalleryCategory(String galleryCategory) {
		this.galleryCategory = galleryCategory;
	}
	
}