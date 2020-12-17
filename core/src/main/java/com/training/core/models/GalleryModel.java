package com.training.core.models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.training.core.constants.Constants;
import com.training.core.helper.TrainingHelper;
import com.training.core.pojos.ProductTilePojo;
import com.training.core.pojos.TilePojo;
import com.training.core.services.ProductGalleryAndLandingService;
import com.training.core.services.TileGalleryAndLandingService;
import com.training.core.utils.PropertyReaderUtils;

/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class GalleryModel {

	@Self
	Resource resource;

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public void setPages(String[] pages) {
		this.pages = pages;
	}

	public void setTileGalleryAndLandingService(TileGalleryAndLandingService tileGalleryAndLandingService) {
		this.tileGalleryAndLandingService = tileGalleryAndLandingService;
	}

	public void setProductGalleryAndLandingService(ProductGalleryAndLandingService productGalleryAndLandingService) {
		this.productGalleryAndLandingService = productGalleryAndLandingService;
	}

	public void setLinkNavigationCheck(Boolean linkNavigationCheck) {
		this.linkNavigationCheck = linkNavigationCheck;
	}

	public void setGalleryCategory(String[] galleryCategory) {
		this.galleryCategory = galleryCategory;
	}

	public void setResolver(ResourceResolver resolver) {
		this.resolver = resolver;
	}

	public void setGalleryFor(String galleryFor) {
		this.galleryFor = galleryFor;
	}

	public void setLandinggridPath(String landinggridPath) {
		this.landinggridPath = landinggridPath;
	}

	public void setCharacterList(List<TilePojo> characterList) {
		this.characterList = characterList;
	}

	public void setHomePagePath(String homePagePath) {
		this.homePagePath = homePagePath;
	}

	@Self
	Node node;
	@Inject
	@Optional
	private String[] pages;
	@Inject
	private TileGalleryAndLandingService tileGalleryAndLandingService;
	@Inject
	ProductGalleryAndLandingService productGalleryAndLandingService;
	@Inject
	private Boolean linkNavigationCheck;
	@Inject
	private String linkNavOption;
	@Inject
	private String[] galleryCategory;
	@Inject
	@Optional
	private String linkTitle;
	@Inject
	private String ctaButtonLink;

	ResourceResolver resolver;
	@Inject
	private String galleryFor;

	private String landinggridPath;
	private static final Logger LOGGER = LoggerFactory.getLogger(GalleryModel.class);

	private List<TilePojo> characterList = new ArrayList<>();

	private List<TilePojo> categoryCharacterList = new LinkedList<>();

	private List<TilePojo> charactesListByDate = new LinkedList<>();

	private List<TilePojo> manualAuthorCharList = new LinkedList<>();

	private List<ProductTilePojo> landingProductList = new ArrayList<>();

	private List<ProductTilePojo> manualProductList = new ArrayList<>();

	private List<ProductTilePojo> categoryProductsList = new LinkedList<>();

	private List<ProductTilePojo> byDateProductsList = new LinkedList<>();

	String homePagePath;

	/**
	 * The init method. which will fetch the list of characters or games or videos
	 * or products
	 * 
	 */
	@PostConstruct
	protected void init() {
		LOGGER.info("GalleryModel init Start");
		characterList.clear();
		try {
			if (resource != null && !resource.getPath().contains("conf")) {
					resolver = resource.getResourceResolver();
					homePagePath=TrainingHelper.getHomePagePath(resource);
					if(homePagePath!=null){
					landinggridPath = homePagePath;
					if (!linkNavigationCheck) {
						linkNavOption = "";
					}
					checkLandingGridPath();
					if (!"products".equals(galleryFor)) {
						characterList = tileGalleryAndLandingService.getAllTiles(landinggridPath, galleryFor, "landing",
								true);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured", e);
		}
		LOGGER.info("GalleryModel init End");
	}

	/**
	 * method to check the Landing Grid Path
	 * 
	 * @return landinggridPath
	 */
	private String checkLandingGridPath() {
		LOGGER.info("checkLandingGridPath method of GalleryModel start");
		if ("characters".equals(galleryFor)) {
			landinggridPath = landinggridPath + PropertyReaderUtils.getCharacterPath()
					+ PropertyReaderUtils.getCharacterLandingPath();

		} else if ("games".equals(galleryFor)) {
			landinggridPath = PropertyReaderUtils.getGamesLandingPath();
		} else if ("products".equals(galleryFor)) {
			// landinggridPath = landinggridPath + PropertyReaderUtils.getProductPath()

			String productPageRootPath = landinggridPath + PropertyReaderUtils.getProductPath()
					+ Constants.JCR_CONTENT_ROOT;
			Resource productPageRootRes = resolver.getResource(productPageRootPath);
			landinggridPath = TrainingHelper.checkProductThumbnailExpFragemnt(resolver, productPageRootRes);
		}
		LOGGER.info("checkLandingGridPath method of GalleryModel end");
		return landinggridPath;
	}

	/**
	 * method to limit the Characters fetched
	 * 
	 * @return charList
	 */
	public List<TilePojo> getCharacterList() {
		List<TilePojo> charList;
		charList = getFixedNumberChar(characterList, 12);
		return charList;
	}

	public String getGalleryCategory() {
		return galleryCategory[0];
	}

	/**
	 * method returns characters specific to category
	 * 
	 * @return categoryCharacterList
	 */
	public List<TilePojo> getCategoryCharacterList() {
		LOGGER.info("GetCategoryCharacterList -> Start");
		categoryCharacterList = new LinkedList<>();

		TagManager tagManager = resource.getResourceResolver().adaptTo(TagManager.class);
		if (galleryCategory != null && tagManager != null) {
			Tag galleryTag = tagManager.resolve(galleryCategory[0]);
			String galleryTagId = galleryTag.getTagID();
			List<TilePojo> charList = tileGalleryAndLandingService.getTilesByDate(homePagePath, galleryFor,
					galleryTagId, resource.getResourceResolver(), true);
			categoryCharacterList = getFixedNumberChar(charList, 12);
		}
		LOGGER.info("GetCategoryCharacterList -> End");
		return categoryCharacterList;
	}

	/**
	 * method returns Products specific to category
	 * 
	 * @return categoryProductsList
	 */
	public List<ProductTilePojo> getCategoryProductsList() {
		LOGGER.info("GetCategoryProductList -> Start");
		categoryProductsList = new ArrayList<>();
		List<ProductTilePojo> allProducts;
		String currentPath = homePagePath + PropertyReaderUtils.getProductPath();
		TagManager tagManager = resource.getResourceResolver().adaptTo(TagManager.class);
		if (galleryCategory != null && tagManager != null) {
			Tag galleryTag = tagManager.resolve(galleryCategory[0]);
			String galleryTagId = galleryTag.getTagID();
			allProducts = productGalleryAndLandingService.getTilesByDate(currentPath, true);
			if (allProducts != null) {
				categoryProductsList = productGalleryAndLandingService.filterProductsByTag(allProducts, galleryTagId);
			}
			if (categoryProductsList.size() > 12) {
				return categoryProductsList.subList(0, 12);
			}

		}
		LOGGER.info("GetCategoryProductList -> End");
		return categoryProductsList;

	}

	/**
	 * 
	 * method returns all characters from specified path with sort by date
	 * 
	 * @return
	 */
	public List<TilePojo> getCharactesListByDate() {
		LOGGER.info("getCharactesListByDate -> Start");
		charactesListByDate = new LinkedList<>();
		List<TilePojo> charList = tileGalleryAndLandingService.getTilesByDate(homePagePath, galleryFor, null,
				resource.getResourceResolver(), true);
		charactesListByDate = getFixedNumberChar(charList, 12);
		LOGGER.info("getCharactesListByDate -> End");
		return charactesListByDate;
	}

	/**
	 * method to fetch the Character or Games or Products in manual mode
	 * 
	 * @return manualAuthorCharList
	 */
	public List<TilePojo> getManualAuthorCharList() {
		LOGGER.info("getManualAuthorCharList -> Start");
		try {
			String galleryOnPage = node.getPath();
			List<TilePojo> charList = tileGalleryAndLandingService.getAllTiles(galleryOnPage, galleryFor, "gallery",
					true);
			manualAuthorCharList = getFixedNumberChar(charList, 12);
		} catch (RepositoryException e) {
			LOGGER.error("RepositoryException Occured ", e);
		}
		LOGGER.info("getManualAuthorCharList -> End");
		return manualAuthorCharList;
	}

	/**
	 * method returns fixed number of characters (number passed by user) from the
	 * original list which consist n number of characters
	 * 
	 * @param originalCharList
	 * @param charNumber
	 * @return charList
	 */
	private List<TilePojo> getFixedNumberChar(List<TilePojo> originalCharList, int charNumber) {
		LOGGER.info("getFixedNumberChar -> Start");
		List<TilePojo> charList;
		if (originalCharList.size() > charNumber) {
			charList = new LinkedList<>();
			for (int i = 0; i < charNumber; i++) {
				charList.add(originalCharList.get(i));
			}
		} else {
			charList = originalCharList;
		}
		
		LOGGER.debug("from getFixedNumberChar of GalleryModel, charList size {}", charList.size());
		
		LOGGER.info("getFixedNumberChar -> End");
		return charList;
	}

	public void setCategoryProductsList(List<ProductTilePojo> categoryProductsList) {
		this.categoryProductsList = categoryProductsList;
	}

	/**
	 * Method to fetch the product details sorting by Date
	 * 
	 * @return byDateProductsList
	 */
	public List<ProductTilePojo> getByDateProductsList() {
		LOGGER.info("getDateProductsList -> Start");
		String currentPath = homePagePath + PropertyReaderUtils.getProductPath();
		byDateProductsList = productGalleryAndLandingService.getTilesByDate(currentPath, true);
		if (byDateProductsList.size() > 12) {
			return byDateProductsList.subList(0, 12);
		}
		
		LOGGER.debug("byDateProductsList size is {}", byDateProductsList.size());
		
		LOGGER.info("getDateProductsList -> End");
		return byDateProductsList;
	}

	public void setByDateProductsList(List<ProductTilePojo> byDateProductsList) {
		this.byDateProductsList = byDateProductsList;
	}

	public String getCtaButtonLink() {
		if (null != linkTitle) {
			return TrainingHelper.checkLink(ctaButtonLink,resource) + '#' + linkTitle.toLowerCase();
		} else {
			return TrainingHelper.checkLink(ctaButtonLink,resource);
		}

	}

	public String getLinkNavOption() {
		return linkNavOption;
	}

	public void setLinkNavOption(String linkNavOption) {
		this.linkNavOption = linkNavOption;
	}

	public void setCategoryCharacterList(List<TilePojo> categoryCharacterList) {
		this.categoryCharacterList = categoryCharacterList;
	}

	public void setCharactesListByDate(List<TilePojo> charactesListByDate) {
		this.charactesListByDate = charactesListByDate;
	}

	/**
	 * method to fetch the products from Landing Node
	 * 
	 * @return landingProductList
	 */
	public List<ProductTilePojo> getLandingProductList() {
		LOGGER.info("getLandingProductList -> Start");
		Resource landingNodeResource = resolver.getResource(landinggridPath);
		if (landingNodeResource != null) {
			ValueMap nodeValues = landingNodeResource.adaptTo(ValueMap.class);
			if (null != nodeValues) {
				pages = nodeValues.get("pages", String[].class);
				String orderProduct = nodeValues.get("orderProduct", String.class);
				if (orderProduct != null) {
					if (orderProduct.equals("manual")) {
						landingProductList = productGalleryAndLandingService.getAllTiles(pages, true);
					} else {
						String currentPath = homePagePath + PropertyReaderUtils.getProductPath();
						landingProductList = productGalleryAndLandingService.getTilesByDate(currentPath, true);
					}
				}
				if (landingProductList.size() > 12) {
					return landingProductList.subList(0, 12);
				}
				
				LOGGER.debug("landingProductList size {}", 
						landingProductList.size());
			}
		}
		LOGGER.info("getLandingProductList -> End");
		return landingProductList;
	}

	public void setLandingProductList(List<ProductTilePojo> landingProductList) {
		this.landingProductList = landingProductList;
	}

	/**
	 * method to fetch the products when authored manually
	 * 
	 * @return manualProductList
	 */
	public List<ProductTilePojo> getManualProductList() {
		LOGGER.info("getManualProductList -> Start");
		manualProductList.clear();
		if (pages != null) {
			manualProductList = productGalleryAndLandingService.getAllTiles(pages, true);
		}

		if (manualProductList.size() > 12) {

			return manualProductList.subList(0, 12);
		}
		
		LOGGER.debug("manualProductList size {}", 
				manualProductList.size());
		
		LOGGER.info("getManualProductList -> End");
		return manualProductList;
	}

	public void setManualProductList(List<ProductTilePojo> manualProductList) {
		this.manualProductList = manualProductList;
	}
}
