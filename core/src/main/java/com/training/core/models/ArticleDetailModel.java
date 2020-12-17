package com.training.core.models;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.PageManager;
import com.training.core.pojos.TagsPojo;
import com.training.core.services.TileGalleryAndLandingService;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ArticleDetailModel {

	private static final Logger LOGGER = LoggerFactory.getLogger(ArticleDetailModel.class);

	@Self
	private Resource resource;

	@Inject
	String[] primaryTags;

	@Inject
	private TileGalleryAndLandingService tileGalleryAndLandingService;
	private List<String> primaryTagTitle;

	@PostConstruct
	protected void init() {
		LOGGER.info("Start of in it method in Article Detail Model");
		if (null != resource) {
			PageManager pageManager = null;
			pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
			if (null != pageManager) {
				List<TagsPojo> pageTags = tileGalleryAndLandingService.getTagRelatedData(primaryTags);
				LOGGER.debug("Article Tags Pojo is {}", pageTags);
				List<String> tagNamesList = new ArrayList<>();
				for (TagsPojo pageTagName : pageTags) {
					tagNamesList.add(pageTagName.getTagTitle());
					LOGGER.debug("Article page tag Title is {}", pageTagName.getTagTitle());
				}
				primaryTagTitle = tagNamesList;
			}
		}
	}

	public List<String> getPrimaryTagTitle() {
		return primaryTagTitle;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public void setPrimaryTags(String[] primaryTags) {
		this.primaryTags = primaryTags;
	}

	public void setTileGalleryAndLandingService(TileGalleryAndLandingService tileGalleryAndLandingService) {
		this.tileGalleryAndLandingService = tileGalleryAndLandingService;
	}

	public void setPrimaryTagTitle(List<String> primaryTagTitle) {
		this.primaryTagTitle = primaryTagTitle;
	}

}