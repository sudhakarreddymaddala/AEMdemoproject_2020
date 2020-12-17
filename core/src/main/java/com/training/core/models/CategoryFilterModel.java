package com.training.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.training.core.helper.TrainingHelper;
import com.training.core.pojos.CategoryFilterPojo;
import com.training.core.pojos.CategoryPojo;
import com.training.core.services.MultifieldReader;
import com.training.core.services.TileGalleryAndLandingService;
import com.training.core.utils.CategoryFilterSlidesUtils;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */

@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CategoryFilterModel {
	@SlingObject
	private SlingHttpServletRequest request;
	@RequestAttribute(name = "url")
	String pagePath;
	@Inject
	@Via("resource")
	@Optional
	private Node categoryDetail;

	@Inject
	private MultifieldReader multifieldReader;

	@Inject
	private TileGalleryAndLandingService tileGalleryAndLandingService;

	private static final Logger LOGGER = LoggerFactory.getLogger(CategoryFilterModel.class);

	private List<CategoryFilterPojo> catItemsList = new ArrayList<>();

	private List<CategoryPojo> categoryList = new ArrayList<>();

	private String homePagePath = "";

	private String categoryFor = "character";

	private boolean displayCategory = false;
	private String slideCount = "10";

	/**
	 * The init method.
	 */
	@PostConstruct
	protected void init() {
		LOGGER.info("CategoryFilterModel init start");
		Map<String, ValueMap> multifieldProperty;
		if (categoryDetail != null) {
			displayCategory = true;
			multifieldProperty = multifieldReader.propertyReader(categoryDetail);
			for (Map.Entry<String, ValueMap> entry : multifieldProperty.entrySet()) {
				CategoryFilterPojo categoryItem = new CategoryFilterPojo();
				String categoryPath = entry.getValue().get("category", String.class);
				if (null != categoryPath) {
					categoryItem.setCategoryPath(categoryPath);
					LOGGER.debug("categoryPath value of CatergoryFilterModel is {}", categoryPath);
				}
				catItemsList.add(categoryItem);
				LOGGER.debug("catItemsList value of CatergoryFilterModel is {}", catItemsList.size());
			}
			categoryList = tileGalleryAndLandingService.getCategories(catItemsList);
			LOGGER.debug("categoryList value of CatergoryFilterModel is {}", categoryList.size());
		}
		ResourceResolver resourceResolver = request.getResourceResolver();
		if (pagePath != null) {
			LOGGER.debug("pagePath value of CatergoryFilterModel is {}", pagePath);
			Resource currentResource = resourceResolver.getResource(pagePath);
			if (currentResource != null) {
				PageManager pageManager = currentResource.getResourceResolver().adaptTo(PageManager.class);
				getHomePagePathAndSlideCount(pageManager,pagePath);
			}
		} else if (request.getRequestURI().contains("experience-fragments")) {
			homePagePath = request.getRequestURI().split(".html")[0];
		}
		Resource resource = request.getResource();
		ValueMap valmap = resource.getValueMap();
		categoryFor = null != valmap.get("categoryFor", String.class) ? valmap.get("categoryFor", String.class)
				: "character";

		LOGGER.info("CategoryFilterModel init end");

	}

	/**
	 * @param pageManager page Manager object
	 * @param pagePath current page path
	 */
	private void getHomePagePathAndSlideCount(PageManager pageManager, String pagePath) {
		LOGGER.info("Start of getHomePagePathAndSlideCount method");
		if (null != pageManager && null != pageManager.getPage(pagePath)) {
			Page page = pageManager.getPage(pagePath);
			if (null != page.getAbsoluteParent(6)) {
				homePagePath = page.getAbsoluteParent(6).getPath();
				LOGGER.debug("homePagePath value of CatergoryFilterModel is {}", homePagePath);
			}
			slideCount = getCategoryFilterSlidesMappings(pagePath, slideCount);
			LOGGER.debug("slideCount value of CatergoryFilterModel is {}", slideCount);
		}
		LOGGER.info("End of getHomePagePathAndSlideCount method");
	}

	/**
	 * Method to fetch the slide count for category filter
	 * 
	 * @param nodePath
	 * @param slideCount
	 * @return
	 */
	public String getCategoryFilterSlidesMappings(String nodePath, String slideCount) {
		LOGGER.info("CategoryFilterModel of method getCategoryFilterSlidesMappings start");
		String[] slideshowValueMapping = CategoryFilterSlidesUtils.getSlideShowValueMapping();
		String brand = TrainingHelper.getBrandName(nodePath);
		LOGGER.debug("brand value of CatergoryFilterModel is {}", brand);
		slideCount = TrainingHelper.getSlideCount(slideCount, slideshowValueMapping, brand);
		LOGGER.info("CategoryFilterModel of method getCategoryFilterSlidesMappings end");
		return slideCount;
	}

	/**
	 * @return This method return list of category filter list
	 */
	public List<CategoryPojo> getCategoryList() {
		return categoryList;
	}

	/**
	 * @return This method returnhome page path
	 */

	public String getHomePagePath() {
		return homePagePath;
	}

	public String getCategoryFor() {
		return categoryFor;
	}

	public void setCatItemsList(List<CategoryFilterPojo> catItemsList) {
		this.catItemsList = catItemsList;
	}

	public void setCategoryList(List<CategoryPojo> categoryList) {
		this.categoryList = categoryList;
	}

	public void setPagePath(String pagePath) {
		this.pagePath = pagePath;
	}

	public void setHomePagePath(String homePagePath) {
		this.homePagePath = homePagePath;
	}

	public void setRequest(SlingHttpServletRequest request) {
		this.request = request;
	}

	public void setCategoryDetail(Node categoryDetail) {
		this.categoryDetail = categoryDetail;
	}

	public void setMultifieldReader(MultifieldReader multifieldReader) {
		this.multifieldReader = multifieldReader;
	}

	public void setTileGalleryAndLandingService(TileGalleryAndLandingService tileGalleryAndLandingService) {
		this.tileGalleryAndLandingService = tileGalleryAndLandingService;
	}

	public boolean isDisplayCategory() {
		return displayCategory;
	}

	public void setDisplayCategory(boolean displayCategory) {
		this.displayCategory = displayCategory;
	}

	public String getSlideCount() {
		return slideCount;
	}

}
