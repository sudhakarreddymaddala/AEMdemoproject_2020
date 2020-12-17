package com.training.core.utils;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Component(service = TrainingSiteConfigurationUtils.class, immediate = true)
@Designate(ocd = TrainingSiteConfigurationUtils.Config.class)
public class TrainingSiteConfigurationUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(TrainingSiteConfigurationUtils.class);
	private static String expFragmentRootPath;
	private static String rootErrorPageName;
	private static String headerExpFragmentName;
	private static String footerExpFragmentName;
	private static String globalfooterExpFragmentName;
	private static String clientlibRootCategoryName;
	private static String gameLandingExpFragmentName;
	private static String categoryFilterExpFragmentName;
	private static String retailerInterstitialPath;
	private static String leavingInterstitialPath;
	private static String interstitialApp;
	private static String productCategoryFilterExpFragmentName;
	private static String productThumbnailGridExpFragmentName;
	private static String productDetailTitleExpFragmentName;
	private static String productPrimaryImageTag;
	private static String[] excludedBrandsFooter;
	private static String[] excludedBrandsCountryDropdown;
	private static String fisherPriceKidsBrandName;
	private static String videoSortOrderProperty;
	private static String fpExpFragmentRootPath;
	private static String rescueExpFgmtRootPath;
	private static String fpRootPath;
	private static String rescueRootPath;

	@Activate
	public void activate(final Config config) {
		expFragmentRootPath = config.expFragmentRootPath();
		rootErrorPageName = config.rootErrorPageName();
		headerExpFragmentName = config.headerExpFragmentName();
		footerExpFragmentName = config.footerExpFragmentName();
		globalfooterExpFragmentName = config.globalfooterExpFragmentName();
		clientlibRootCategoryName = config.clientlibRootCategoryName();
		gameLandingExpFragmentName = config.gameLandingExpFragmentName();
		categoryFilterExpFragmentName = config.categoryFilterExpFragmentName();
		retailerInterstitialPath = config.retailerInterstitialPath();
		leavingInterstitialPath = config.leavingInterstitialPath();
		interstitialApp = config.interstitialApp();
		productCategoryFilterExpFragmentName = config.productCategoryFilterExpFragmentName();
		productThumbnailGridExpFragmentName = config.productThumbnailGridExpFragmentName();
		productDetailTitleExpFragmentName = config.productDetailTitleExpFragmentName();
		productPrimaryImageTag = config.productPrimaryImageTag();
		excludedBrandsFooter = config.excludedBrandsFooter();
		excludedBrandsCountryDropdown = config.excludedBrandsCountryDropdown();
		fisherPriceKidsBrandName = config.fisherPriceKidsBrandName();
		videoSortOrderProperty = config.videoSortOrderProperty();
		LOGGER.info("Experince Fragment Root Path is {}", expFragmentRootPath);
	}

	@ObjectClassDefinition(name = "training Site Common Configuration")
	public @interface Config {
		@AttributeDefinition(name = "Training Experience Fragment Root Path ", description = "Experience Fragment Root Path for Training")
		String expFragmentRootPath() default "/content/experience-fragments/training/";

		@AttributeDefinition(name = "Root Error Page Name")
		String rootErrorPageName() default "/error/";

		@AttributeDefinition(name = "Header Experience Fragment Name")
		String headerExpFragmentName() default "header";

		@AttributeDefinition(name = "Footer Experience Fragment Name")
		String footerExpFragmentName() default "footer";

		@AttributeDefinition(name = "Root Clientlibrary Name")
		String clientlibRootCategoryName() default "training.";

		@AttributeDefinition(name = "Game Landing Grid Experience Fragment Name")
		String gameLandingExpFragmentName() default "game_landinggrid";

		@AttributeDefinition(name = "Character Category Filter Experience Fragment Name")
		String categoryFilterExpFragmentName() default "character_category_filter";

		@AttributeDefinition(name = "Retailer Interstitial Experience Fragment Name")
		String retailerInterstitialPath() default "interstitial-retailer";

		@AttributeDefinition(name = "Interstitial Leaving the Site Experience Fragment Name")
		String leavingInterstitialPath() default "interstitial-leavingthesite";

		@AttributeDefinition(name = "Interstitial App Experience Fragment Name")
		String interstitialApp() default "interstitial-app";

		@AttributeDefinition(name = "Product Category Filter Experience Fragment Name")
		String productCategoryFilterExpFragmentName() default "product-category-filter";

		@AttributeDefinition(name = "Product Thumbnail Grid Experience Fragment Name")
		String productThumbnailGridExpFragmentName() default "product-thumbnail-grid";

		@AttributeDefinition(name = "Product Detail Grid Title Experience Fragment Name")
		String productDetailTitleExpFragmentName() default "productdetailgrid-title";

		@AttributeDefinition(name = "Products Asset Primary Image Tag")
		String productPrimaryImageTag() default "primaryAsset";

		@AttributeDefinition(name = "Global US Footer Experince Fragment Name")
		String globalfooterExpFragmentName() default "footer-global_en-us";

		@AttributeDefinition(name = "Excluded brands for footer", description = "Brands to exclude from Global US Footer Logic")
		String[] excludedBrandsFooter();

		@AttributeDefinition(name = "Excluded brands for country dropdown", description = "Brands to exclude country dropdown from footer")
		String[] excludedBrandsCountryDropdown();

		@AttributeDefinition(name = "Brand Name for Fisher Price Kids")
		String fisherPriceKidsBrandName();

		@AttributeDefinition(name = "Video Sort Order Property")
		String videoSortOrderProperty() default "jcr:lastModified";

	}

	/**
	 * @return expFragmentRootPath
	 */
	public static String getExpFragmentRootPath() {
		return expFragmentRootPath;
	}

	/**
	 * @return rootErrorPageName
	 */
	public static String getRootErrorPageName() {
		return rootErrorPageName;
	}

	/**
	 * @return headerExpFragmentName
	 */
	public static String getHeaderExpFragmentName() {
		return headerExpFragmentName;
	}

	/**
	 * @return headerExpFragmentName
	 */
	public static String getFooterExpFragmentName() {
		return footerExpFragmentName;
	}

	/**
	 * @return clientlibRootCategoryName
	 */
	public static String getClientlibRootCategoryName() {
		return clientlibRootCategoryName;
	}

	/**
	 * @return gameLandingExpFragmentName
	 */
	public static String getGameLandingExpFragmentName() {
		return gameLandingExpFragmentName;
	}

	/**
	 * @return categoryFilterExpFragmentName
	 */
	public static String getCategoryFilterExpFragmentName() {
		return categoryFilterExpFragmentName;
	}

	public static String getRetailerInterstitialPath() {
		return retailerInterstitialPath;
	}

	public static String getProductCategoryFilterExpFragmentName() {
		return productCategoryFilterExpFragmentName;
	}

	public static String getProductThumbnailGridExpFragmentName() {
		return productThumbnailGridExpFragmentName;
	}

	public static String getProductDetailTitleExpFragmentName() {
		return productDetailTitleExpFragmentName;
	}

	public static String getProductPrimaryImageTag() {
		return productPrimaryImageTag;
	}

	public static String getLeavingInterstitialPath() {
		return leavingInterstitialPath;
	}

	public static String getInterstitialApp() {
		return interstitialApp;
	}

	public static String getGlobalfooterExpFragmentName() {
		return globalfooterExpFragmentName;
	}

	public static String[] getExcludedBrandsFooter() {
		return excludedBrandsFooter;
	}

	public static String[] getExcludedBrandsCountryDropdown() {
		return excludedBrandsCountryDropdown;
	}

	public static String getFisherPriceKidsBrandName() {
		return fisherPriceKidsBrandName;
	}

	public static String getVideoSortOrderProperty() {
		return videoSortOrderProperty;
	}

	public static String getFpExpFragmentRootPath() {
		return fpExpFragmentRootPath;
	}

	public static String getRescueExpFgmtRootPath() {
		return rescueExpFgmtRootPath;
	}

	public static String getFpRootPath() {
		return fpRootPath;
	}

	public static String getRescueRootPath() {
		return rescueRootPath;
	}

}
