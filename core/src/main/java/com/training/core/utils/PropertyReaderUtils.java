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
@Component(service = PropertyReaderUtils.class, immediate = true)
@Designate(ocd = PropertyReaderUtils.Config.class)
public class PropertyReaderUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertyReaderUtils.class);
	private static String scriptUrl;
	private static String trainingPath;
	private static String trainingDamPath;
	private static String videoDamPath;
	private static String productPath;
	private static String videoPath;
	private static String characterLandingPath;
	private static String characterPath;
	private static String characterResourceType;
	private static String gameResourceType;
	private static String productResourceType;
	private static String gamePath;
	private static String downloadInterstitialApp;
	private static String gamesLandingPath;
	private static String damGlobalPath;
	private static String homePath;
	private static String productLandingPath;
	private static String commerceProductsPath;
	private static String articleResourceType;
	private static String articleSortOrderProperty;
	private static String rescuePath;
	private static String imageResourceType;
	private static String categoryNavResourceType;
	private static String otherNavResourceType;

	@Activate
	public void activate(final Config config) {
		scriptUrl = config.scriptUrl();
		trainingPath = config.trainingPath();
		trainingDamPath = config.trainingDamPath();
		videoDamPath = config.videoDamPath();
		productPath = config.productPath();
		videoPath = config.videoPath();
		characterLandingPath = config.characterLandingPath();
		characterPath = config.characterPath();
		characterResourceType = config.characterResourceType();
		gameResourceType = config.gameResourceType();
		productResourceType = config.productResourceType();
		gamePath = config.gamePath();
		downloadInterstitialApp = config.downloadInterstitialApp();
		gamesLandingPath = config.gamesLandingPath();
		damGlobalPath = config.damGlobalPath();
		homePath = config.homePath();
		productLandingPath = config.productLandingPath();
		commerceProductsPath = config.commerceProductsPath();
		articleResourceType = config.articleResourceType();
		articleSortOrderProperty = config.articleSortOrderProperty();
		imageResourceType = config.imageResourceType();
		categoryNavResourceType = config.categoryNavResourceType();
		otherNavResourceType = config.otherNavResourceType();
		LOGGER.debug("Path of script {}", scriptUrl);
	}

	@ObjectClassDefinition(name = "training Properties Configuration")
	public @interface Config {
		@AttributeDefinition(name = "Analytics Script Url", description = "Please enter script URL "
				+ "for analytics tracking ")
		String scriptUrl();

		@AttributeDefinition(name = "training Path")
		String trainingPath();

		@AttributeDefinition(name = "training Dam Assets Path")
		String trainingDamPath();

		@AttributeDefinition(name = "training Videos Path")
		String videoDamPath();

		@AttributeDefinition(name = "training Products Landing Path")
		String productPath();

		@AttributeDefinition(name = "training Video Landing Path")
		String videoPath();

		@AttributeDefinition(name = "training Character Path")
		String characterLandingPath();

		@AttributeDefinition(name = "training Game Path")
		String gamePath();

		@AttributeDefinition(name = "training Character Landing Path")
		String characterPath();

		@AttributeDefinition(name = "training Character Resource Type")
		String characterResourceType();

		@AttributeDefinition(name = "training Game Resource Type")
		String gameResourceType();

		@AttributeDefinition(name = "training Product Resource Type")
		String productResourceType();

		@AttributeDefinition(name = "Download Image Path")
		String downloadInterstitialApp();

		@AttributeDefinition(name = "Games Landing Grid Experience Fragment Path")
		String gamesLandingPath();

		@AttributeDefinition(name = "Dam Assets Global Path")
		String damGlobalPath();

		@AttributeDefinition(name = "home Path")
		String homePath();

		@AttributeDefinition(name = " Product Landing Path")
		String productLandingPath();

		@AttributeDefinition(name = "Path for Products Creation")
		String commerceProductsPath();

		@AttributeDefinition(name = "training Article Resource Type")
		String articleResourceType();

		@AttributeDefinition(name = "trainingtrainingicle OrderType")
		String articleSortOrderProperty();

		@AttributeDefinition(name = "Image Resource Type")
		String imageResourceType();

		@AttributeDefinition(name = "Shop By Category Component Resource Type")
		String categoryNavResourceType();

		@AttributeDefinition(name = "Navigation Component Resource Type")
		String otherNavResourceType();
	}

	public static String getTrainingPath() {
		return trainingPath;
	}

	public static void setTrainingPath(String trainingPath) {
		PropertyReaderUtils.trainingPath = trainingPath;
	}

	public static String getTrainingDamPath() {
		return trainingDamPath;
	}

	public static void setTrainingDamPath(String trainingDamPath) {
		PropertyReaderUtils.trainingDamPath = trainingDamPath;
	}

	public String getScriptUrl() {
		return scriptUrl;
	}

	public static String getVideoDamPath() {
		return videoDamPath;
	}

	public static String getProductPath() {
		return productPath;
	}

	public static String getVideoPath() {
		return videoPath;
	}

	public static String getCharacterLandingPath() {
		return characterLandingPath;
	}

	public static String getCharacterPath() {
		return characterPath;
	}

	public static String getCharacterResourceType() {
		return characterResourceType;
	}

	public static String getGameResourceType() {
		return gameResourceType;
	}

	public static String getProductResourceType() {
		return productResourceType;
	}

	public static String getGamePath() {
		return gamePath;
	}

	public static String getDownloadInterstitialApp() {
		return downloadInterstitialApp;
	}

	public static String getGamesLandingPath() {
		return gamesLandingPath;
	}

	public static String getDamGlobalPath() {
		return damGlobalPath;
	}

	public static String getHomePath() {
		return homePath;
	}

	public static String getProductLandingPath() {
		return productLandingPath;
	}

	public static String getCommerceProductsPath() {
		return commerceProductsPath;
	}

	public static String getArticleResourceType() {
		return articleResourceType;
	}

	public static String getArticleSortOrderProperty() {
		return articleSortOrderProperty;
	}

	public static String getRescuePath() {
		return rescuePath;
	}

	public static String getImageResourceType() {
		return imageResourceType;
	}

	public static String getCategoryNavResourceType() {
		return categoryNavResourceType;
	}

	public static String getOtherNavResourceType() {
		return otherNavResourceType;
	}

}
