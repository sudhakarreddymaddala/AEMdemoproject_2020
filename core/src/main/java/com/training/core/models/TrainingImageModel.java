package com.training.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.training.core.pojos.TilePojo;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TrainingImageModel {

	@Inject
	String detailpageMapping;
	@Optional
	@Inject
	String tileThumbnail;
	@Optional
	@Inject
	String tileAltTxt;
	@Self
	Resource resource;
	private static final Logger LOGGER = LoggerFactory.getLogger(TrainingImageModel.class);
	private TilePojo tilePojo = new TilePojo();

	/**
	 * The init method to fetch the Tile details
	 */
	@PostConstruct
	protected void init() {
		LOGGER.info("PlayImageModel start of init");
		try {
			if (resource != null) {
				ResourceResolver resolver = resource.getResourceResolver();
				if (detailpageMapping != null) {
					fetchTitle(resolver);
					if (tileThumbnail == null) {
						fetchThumbnailDetails(resolver);
					}
				}
			}

		} catch (

		Exception e) {
			LOGGER.error("Exception occured in init method{}", e.getMessage());
		}
		LOGGER.info("PlayImageModel end of init");
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public void setTilePojo(TilePojo tilePojo) {
		this.tilePojo = tilePojo;
	}

	public TilePojo getTilePojo() {
		return tilePojo;
	}

	public String getTileThumbnail() {
		return tileThumbnail;
	}

	public void setTileThumbnail(String tileThumbnail) {
		this.tileThumbnail = tileThumbnail;
	}

	public String getTileAltTxt() {
		return tileAltTxt;
	}

	public void setTileAltTxt(String tileAltTxt) {
		this.tileAltTxt = tileAltTxt;
	}

	/**
	 * Method to fetch the thumbnail details for Characters or Games
	 * 
	 * @param resolver
	 * @throws RepositoryException
	 */
	private void fetchThumbnailDetails(ResourceResolver resolver) throws RepositoryException {
		LOGGER.info("Start of fetchThumbnailDetails method");
		Resource pageContentResource = resolver
				.getResource(detailpageMapping + com.training.core.constants.Constants.JCR_CONTENT_ROOT);
		if (pageContentResource != null) {
			LOGGER.debug("pageContentResource is {}",pageContentResource.getPath());
			Node pageContentNode = pageContentResource.adaptTo(Node.class);
			if (pageContentNode != null) {
				boolean characterCheck = pageContentNode.hasNode("character");
				Node tileNode;
				if (characterCheck) {
					tileNode = pageContentNode.getNode("character");
					tileThumbnail = tileNode.getProperty("tileThumbnail").getString();
					tileAltTxt = tileNode.getProperty("tileAltTxt").getString();
				} else {
					Node pageRootNode = pageContentNode.getParent();
					boolean gameCheck = pageRootNode.hasNode("game");
					if (gameCheck) {
						tileNode = pageRootNode.getNode("game");
						tileThumbnail = tileNode.getProperty("tileThumbnail").getString();
						tileAltTxt = tileNode.getProperty("tileAltTxt").getString();
					}
				}
			}
		}
		LOGGER.info("End of fetchThumbnailDetails method");
	}

	/**
	 * Method to fetch the title
	 * 
	 * @param resolver
	 * @throws RepositoryException
	 */
	private void fetchTitle(ResourceResolver resolver) throws RepositoryException {
		LOGGER.info("fetchTitle method of CharacterImageModel ---> Start");
		Resource detailResource = resolver
				.getResource(detailpageMapping + com.training.core.constants.Constants.JCR_CONTENT);
		if (detailResource != null) {
			Node detailNode = detailResource.adaptTo(Node.class);
			if (detailNode != null) {
				String title = detailNode.hasProperty("navTitle") ? detailNode.getProperty("navTitle").getString()
						: null;
				if (title == null) {
					title = detailNode.getProperty("jcr:title").getString();
				}
				tilePojo.setTileTitle(title);
			}
		}
		LOGGER.info("fetchTitle method of CharacterImageModel ---> End");
	}

	public void setDetailpageMapping(String detailpageMapping) {
		this.detailpageMapping = detailpageMapping;

	}
}
