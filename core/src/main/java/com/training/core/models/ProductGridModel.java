package com.training.core.models;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.training.core.helper.TrainingHelper;
import com.training.core.utils.PropertyReaderUtils;

/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductGridModel {

	@SlingObject
	private SlingHttpServletRequest request;

	@RequestAttribute(name = "url")
	String pagePath;

	private String productThumbnailGridNodePath;

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductGridModel.class);
	private String landingBackOption;
	private String landingBgReference;
	private String landingBgColor;

	/**
	 * The init method.
	 */
	@PostConstruct
	protected void init() {
		LOGGER.info("ProductGridModel init start");
		Resource resource = request.getResource();
		if (null != pagePath) {
			ResourceResolver resolver = resource.getResourceResolver();
			PageManager pageManager = resolver.adaptTo(PageManager.class);
			if (null != pageManager) {
				Page currentPage = pageManager.getPage(pagePath);
				if (null != currentPage) {
					Page homePage = currentPage.getAbsoluteParent(5);
					if (null != homePage) {
						String hoemPagePath = homePage.getPath();
						String productsPagePath = hoemPagePath + PropertyReaderUtils.getProductPath()
								+ "/jcr:content/root";
						Resource productsPageres = resource.getResourceResolver().getResource(productsPagePath);
						if (null != productsPageres) {
							productThumbnailGridNodePath = TrainingHelper
									.checkProductThumbnailExpFragemnt(resource.getResourceResolver(), productsPageres);
							Resource landingNodeResource = resolver.getResource(productThumbnailGridNodePath);
							fetchLandingbackGroundDetails(landingNodeResource);
						}
					}
				}
			}

		}
		LOGGER.info("ProductGridModel init end");
	}

	/**
	 * Method to fetch Product Background Details
	 * 
	 * @param landingNodeResource
	 */
	private void fetchLandingbackGroundDetails(Resource landingNodeResource) {
		if (null != landingNodeResource) {
			ValueMap nodeValues = landingNodeResource.adaptTo(ValueMap.class);
			if (null != nodeValues) {
				landingBackOption = nodeValues.get("landingBackOption", String.class);
				landingBgReference = nodeValues.get("landingBgReference", String.class);
				landingBgColor = nodeValues.get("landingBgColor", String.class);
			}
		}
	}

	public String getLandingBackOption() {
		return landingBackOption;
	}

	public void setLandingBackOption(String landingBackOption) {
		this.landingBackOption = landingBackOption;
	}

	public String getLandingBgReference() {
		return landingBgReference;
	}

	public void setLandingBgReference(String landingBgReference) {
		this.landingBgReference = landingBgReference;
	}

	public String getLandingBgColor() {
		return landingBgColor;
	}

	public void setLandingBgColor(String landingBgColor) {
		this.landingBgColor = landingBgColor;
	}

	public void setRequest(SlingHttpServletRequest request) {
		this.request = request;
	}

	public void setPagePath(String pagePath) {
		this.pagePath = pagePath;
	}

	public void setProductThumbnailGridNodePath(String productThumbnailGridNodePath) {
		this.productThumbnailGridNodePath = productThumbnailGridNodePath;
	}

}
