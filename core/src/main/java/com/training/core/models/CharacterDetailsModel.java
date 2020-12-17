package com.training.core.models;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.training.core.helper.TrainingHelper;
import com.training.core.pojos.TilePojo;
import com.training.core.services.TileGalleryAndLandingService;
import com.training.core.utils.CategoryFilterSlidesUtils;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CharacterDetailsModel {
	@Self
	Resource resource;
	@Self
	Node node;
	@Inject
	private TileGalleryAndLandingService tileGalleryAndLandingService;
	private String homePagePath;
	private String prevCharacter;
	private String nextCharacter;
	private String categoryName;
	private String manual = "manual";
	private String automatic = "automatic";
	boolean linkToPages = true;
	String currentPath;
	String rootPath;
	String orderCharacter;
	private String slideCount = "6";
	private static final Logger LOGGER = LoggerFactory.getLogger(CharacterDetailsModel.class);

	private List<TilePojo> tileList = new ArrayList<>();

	/**
	 * The init method. which will fetch the list of characters and links to
	 * previous character and next character.
	 */
	@PostConstruct
	protected void init() {

		LOGGER.info("CharacterDetailsModel init start");
		try {
			if (resource != null) {
				PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
				if (pageManager != null) {
					Page page = pageManager.getContainingPage(resource);
					if (page != null) {
						currentPath = page.getPath();
						categoryName = page.getParent().getTitle();
						rootPath = page.getAbsoluteParent(6).getPath();
						homePagePath = rootPath + "/jcr:content/root/characterlandinggrid/";
						LOGGER.debug("currentpath value of CharacterDetailsModel is {}",currentPath);
						LOGGER.debug("categoryName value of CharacterDetailsModel is {}",categoryName);
						LOGGER.debug("rootPath value of CharacterDetailsModel is {}",rootPath);
						LOGGER.debug("homePagePath value of CharacterDetailsModel is {}",homePagePath);
						ResourceResolver resolver = resource.getResourceResolver();
						Resource nodeResource = resolver.getResource(homePagePath);
						if (nodeResource != null) {
							getDetailNode(nodeResource);
						}
						setPrevNextLinks(tileList, currentPath,resource);
						slideCount = getCharacterCarouselSlidesMappings(resource.getPath(),slideCount);
						LOGGER.debug("slideCount value of CharacterDetailsModel is {}",slideCount);
					}
				}
			}

		} catch (Exception e) {
			LOGGER.debug(e.getMessage());
		}
		LOGGER.info("CharacterDetailsModel init end");
	}
	
	/**
	 * Method to fetch the slide count for character Carousel slides
	 * 
	 * @param nodePath
	 * @param slideCount
	 * @return
	 */
	public String getCharacterCarouselSlidesMappings(String nodePath,String slideCount) {
		LOGGER.info("getCharacterCarouselSlidesMappings method of CharacterDetailsModel start");
		String[] slideshowValueMapping = CategoryFilterSlidesUtils.getCharacterSlidesValueMapping();
		String brand = TrainingHelper.getBrandName(nodePath);
		LOGGER.debug("brand value of CharacterDetailsModel is {}",brand);
		slideCount = TrainingHelper.getSlideCount(slideCount, slideshowValueMapping, brand);
		LOGGER.info("getCharacterCarouselSlidesMappings method of CharacterDetailsModel end");
		return slideCount;
	}

	public void setHomePagePath(String homePagePath) {
		this.homePagePath = homePagePath;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public void setTileGalleryAndLandingService(TileGalleryAndLandingService tileGalleryAndLandingService) {
		this.tileGalleryAndLandingService = tileGalleryAndLandingService;
	}

	public void setPrevCharacter(String prevCharacter) {
		this.prevCharacter = prevCharacter;
	}

	public void setNextCharacter(String nextCharacter) {
		this.nextCharacter = nextCharacter;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public void setManual(String manual) {
		this.manual = manual;
	}

	public void setAutomatic(String automatic) {
		this.automatic = automatic;
	}

	public void setCurrentPath(String currentPath) {
		this.currentPath = currentPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public void setOrderCharacter(String orderCharacter) {
		this.orderCharacter = orderCharacter;
	}

	public void setTileList(List<TilePojo> tileList) {
		this.tileList = tileList;
	}

	public List<TilePojo> getTileList() {
		return tileList;
	}

	public String getHomePagePath() {
		return homePagePath;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public String getPrevCharacter() {
		LOGGER.debug("Get Prev Character {} ", prevCharacter);		return prevCharacter;
	}

	public String getNextCharacter() {
		LOGGER.debug("Get Next Character {} ", nextCharacter);		return nextCharacter;
	}
	
	public String getSlideCount() {
		return slideCount;
	}

	private void setPrevNextLinks(List<TilePojo> tileList, String currentPath,Resource resource) {
		LOGGER.info("setPrevNextLinks of CharacterDetailModel method start");
		if (tileList != null) {
			currentPath = TrainingHelper.checkLink(currentPath, resource);
			LOGGER.debug("currentPath value of CharacterDetailsModel is {}",currentPath);
			for (TilePojo characterPojo : tileList) {
				String tempPath = characterPojo.getTilePath();
				LOGGER.debug("tempPath value of CharacterDetailsModel is {}",tempPath);
				Boolean checkPath = currentPath.equals(tempPath);
				if (checkPath) {
					int currentIndex = tileList.indexOf(characterPojo);
					int lastIndex = tileList.size();
					if (currentIndex != 0) {
						TilePojo prevPojo;
						prevPojo = tileList.get(currentIndex - 1);
						prevCharacter = prevPojo.getTilePath() + "#detail";
					}
					if (currentIndex != lastIndex - 1) {
						TilePojo nextPojo;
						nextPojo = tileList.get(currentIndex + 1);
						nextCharacter = nextPojo.getTilePath() + "#detail";
					}
				}
			}
		}
		LOGGER.info("setPrevNextLinks of CharacterDetailModel method end");
	}

	private void getCharacterDetails(String orderCharacter) {
		LOGGER.info("getCharacterDetails of CharacterDetailModel method start");
		if (orderCharacter != null) {
			if (tileList != null) {
				tileList.clear();
			}
			String tileType = "characters";
			String tilePage = "landing";
			if (orderCharacter.equals(manual)) {
				tileList = tileGalleryAndLandingService.getAllTiles(homePagePath, tileType, tilePage, linkToPages);
			} else if (orderCharacter.equals(automatic)) {
				tileList = tileGalleryAndLandingService.getTilesByDate(rootPath, tileType, null,
						resource.getResourceResolver(), linkToPages);
				LOGGER.debug("tileList size of CharacterDetailsModel is {}",tileList.size());
				
			}
		}
		LOGGER.info("getCharacterDetails of CharacterDetailModel method end");
	}

	private void getDetailNode(Resource nodeResource) throws RepositoryException {
		LOGGER.info("getDetailNode of CharacterDetailModel method start");
		Node detailNode = nodeResource.adaptTo(Node.class);
		if (detailNode != null) {
			orderCharacter = detailNode.getProperty("orderCharacter").getString();
			LOGGER.debug("orderCharacter value of CharacterDetailsModel is {}",orderCharacter);
			if (detailNode.hasProperty("linkToPages")) {
				linkToPages = detailNode.getProperty("linkToPages").getBoolean();
			}
			getCharacterDetails(orderCharacter);
		}
		LOGGER.info("getDetailNode of CharacterDetailModel method end");
	}
}
