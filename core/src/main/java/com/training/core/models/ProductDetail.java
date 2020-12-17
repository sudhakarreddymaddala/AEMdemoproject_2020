package com.training.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.training.core.constants.Constants;
import com.training.core.pojos.ProductTilePojo;
import com.training.core.services.ProductGalleryAndLandingService;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ProductDetail {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductDetail.class);

	@Self
	Resource resource;

	@Inject
	@Optional
	private String productPath;

	@Inject
	ProductGalleryAndLandingService productGalleryAndLandingService;

	ProductTilePojo productDetails = new ProductTilePojo();

	private String analyticsCategories;

	private boolean isEnglishLocale;

	public ProductTilePojo getProductDetails() {
		return productDetails;
	}

	public void setProductDetails(ProductTilePojo productDetail) {
		this.productDetails = productDetail;
	}

	/**
	 * The init method to fetch Product Details
	 */
	@PostConstruct
	protected void init() {
		LOGGER.info("this is start of init method of Product Detail");
		if (productPath != null && resource != null) {
			
			String productPageUrl = getProductPagePath(resource);
			productDetails = productGalleryAndLandingService.getProductTileDetails(productPath,
					resource.getResourceResolver(), productPageUrl, true,
					productDetails, true);
			analyticsCategories = "";
			String appendString = "/";
			int count = 0;

			// logic for getting categories for analytics
			if (productDetails.getProductTags() != null) {
				int size = productDetails.getProductTags().size();
				StringBuilder analyticsCategoryString = new StringBuilder();
				for (String category : productDetails.getProductTags()) {

					if (size <= 1 || count == size - 1) {
						appendString = "";
					}
					analyticsCategoryString = analyticsCategoryString
							.append(category.substring(category.lastIndexOf('/') + 1) + appendString);
					count++;
				}
				analyticsCategories = analyticsCategoryString.toString();

			}

			if (resource.getPath().contains("en-us")) {
				setEnglishLocale(true);
			}

		}
		LOGGER.info("this is end of init method of Product Detail");
	}

	/** checking product detail component is used in homepage or product detail pages
	 * @param resource
	 * @return
	 */
	private String getProductPagePath(Resource resource) {
		// TODO Auto-generated method stub
		String productPageUrl = null;
		if(resource.getPath().contains(Constants.JCR_PRODUCT_NODE)) {
			productPageUrl = resource.getPath().replace(Constants.JCR_PRODUCT_NODE, "");
		}else {
			productPageUrl = resource.getPath().replace(Constants.JCR_ROOT_PRODUCT_NODE, "");
		}
		return productPageUrl;
		
		
	}

	public boolean isEnglishLocale() {
		return isEnglishLocale;
	}

	public void setEnglishLocale(boolean isEnglishLocale) {
		this.isEnglishLocale = isEnglishLocale;
	}

	public String getAnalyticsCategories() {
		return analyticsCategories;
	}

	public void setAnalyticsCategories(String analyticsCategories) {
		this.analyticsCategories = analyticsCategories;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public void setProductPath(String productPath) {
		this.productPath = productPath;
	}

	public void setProductGalleryAndLandingService(ProductGalleryAndLandingService productGalleryAndLandingService) {
		this.productGalleryAndLandingService = productGalleryAndLandingService;
	}

}
