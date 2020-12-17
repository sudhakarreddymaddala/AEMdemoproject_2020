package com.training.core.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.JsonObject;
import com.training.core.constants.Constants;
import com.training.core.helper.TrainingHelper;
import com.training.core.pojos.HrefLangPojo;
import com.training.core.services.MultifieldReader;
import com.training.core.utils.PropertyReaderUtils;
import com.training.core.utils.TrainingSiteConfigurationUtils;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TrainingPageModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(TrainingPageModel.class);
	@Inject
	private MultifieldReader multifieldReader;

	public void setMultifieldReader(MultifieldReader multifieldReader) {
		this.multifieldReader = multifieldReader;
	}

	public void setPropertyReaderUtils(PropertyReaderUtils propertyReaderUtils) {
		this.propertyReaderUtils = propertyReaderUtils;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	@Inject
	private PropertyReaderUtils propertyReaderUtils;

	@Self
	private Resource resource;

	private List<HrefLangPojo> hrefLangList = new ArrayList<>();
	String languageMasters = "language-masters";
	private static String slashHome = "/home";
	private static String cqRedirectTarget = "cq:redirectTarget";
	private String propertiesJson;
	private String scriptUrl;
	private String clientlibCategory;
	private String headerPath;
	private String footerPath;
	private String gameLandingGridPath;
	private String characterCategoryFilterPath;
	private String retailerInterstitialPath;
	private String leavingInterstitialPath;
	private String interstitialApp;
	private String productThumbnailGridFragPath;
	private String businessSiteName;
	private String siteCountry;
	private String productCategoryFilterPath;
	private String productDetailGridTitleFragPath;
	private String pageLocale;
	private String keywordCommaSeparated;
	private String brandName;
	private String adobeTrackingNameForPage;
	private String rescueBrandName;
	private String title;
	String homePagePath;
	private String pageName;
	private String parentPageType;
	String currentPagePath;
	String rescueParentName = StringUtils.EMPTY;
	String expFrLocaleRH = "";
	int leftIndexShiftForSiteWOParentName;

	/**
	 * The init method to fetch the page level properties
	 */
	@PostConstruct
	protected void init() {
		if (null != resource && !resource.getPath().contains("conf/training/settings/wcm/templates")) {
			leftIndexShiftForSiteWOParentName = TrainingHelper.leftIndexShiftForSiteWOParentName(resource);
			pageLocale = TrainingHelper.getPageLocale(resource.getPath());
			if (TrainingHelper.checkIfSiteisTraining(resource.getPath())) {
				brandName = TrainingHelper.getBrandName(resource.getPath());
			} else {
				brandName = TrainingHelper.getBrandName(resource);
			}
			Resource analyticsNodeResource = resource.getResourceResolver()
					.getResource(resource.getPath() + "/analyticsProperties");
			Node analyticsNode = null;
			Map<String, ValueMap> stringValueMapLinkedHashMap = new HashMap<>();
			JsonObject jsonObject = new JsonObject();
			businessSiteName = brandName;
			if (analyticsNodeResource != null) {
				analyticsNode = analyticsNodeResource.adaptTo(Node.class);
				getPropertiesJsonResponseFromAnalyticsNode(analyticsNode, stringValueMapLinkedHashMap, jsonObject);
			}
			scriptUrl = propertyReaderUtils.getScriptUrl();

			fetchTitleAndSeoDetails();

			if (TrainingHelper.checkIfSiteisTraining(resource.getPath())
					&& !resource.getPath().contains(TrainingSiteConfigurationUtils.getRootErrorPageName())
					&& !resource.getPath().contains(TrainingSiteConfigurationUtils.getExpFragmentRootPath())) {
				getHrefLangPropertyList();
			}
			if (!resource.getPath().equalsIgnoreCase(PropertyReaderUtils.getTrainingPath() + Constants.JCR_CONTENT)
					&& !resource.getPath().contains(TrainingSiteConfigurationUtils.getExpFragmentRootPath())) {

				String sitesRootPath = "";
				if (TrainingHelper.checkIfSiteisTraining(resource.getPath())) {
					sitesRootPath = PropertyReaderUtils.getTrainingPath()
							+ TrainingHelper.getBrandName(resource.getPath());
				} else {
					int indexBrandName = resource.getPath().indexOf(brandName);
					sitesRootPath = resource.getPath().substring(0, indexBrandName + brandName.length());
				}

				String currentResPath = resource.getPath();

				currentPagePath = currentResPath.replace("jcr:content", "");

				if (TrainingHelper.checkIfSiteisTraining(resource.getPath())) {
					fetchSiteCountry(currentPagePath);
					getAllExperienceFragmentPaths(sitesRootPath, currentPagePath);
				}
			}

			InheritanceValueMap inheritanceValueMap = new HierarchyNodeInheritanceValueMap(resource);
			clientlibCategory = inheritanceValueMap.getInherited("clientlibCategory", String.class);
			clientlibCategory = checkClientLibCategory();
			adobeTrackingNameForPage = inheritanceValueMap.getInherited("adobeTrackingNameForPage", String.class);
			getPageNameAndParentPageType(resource);

		}

	}

	private void getPropertiesJsonResponseFromAnalyticsNode(Node analyticsNode,
			Map<String, ValueMap> stringValueMapLinkedHashMap, JsonObject jsonObject) {
		if (null != analyticsNode) {
			stringValueMapLinkedHashMap = multifieldReader.propertyReader(analyticsNode);
		}
		for (Map.Entry<String, ValueMap> mapEntry : stringValueMapLinkedHashMap.entrySet()) {
			jsonObject.addProperty(mapEntry.getValue().get("propertyName").toString(),
					mapEntry.getValue().get("propertyValue").toString());
		}

		propertiesJson = jsonObject.toString();
	}

	private String checkClientLibCategory() {

		if (TrainingHelper.checkIfSiteisTraining(resource.getPath())) {
			if (StringUtils.isBlank(clientlibCategory)) {
				clientlibCategory = TrainingSiteConfigurationUtils.getClientlibRootCategoryName() + brandName;
			}
		} else {
			if (StringUtils.isBlank(clientlibCategory)) {
				clientlibCategory = "clientlib." + brandName;
			}
		}

		return clientlibCategory;
	}

	private void getAllExperienceFragmentPaths(String sitesRootPath, String currentPagePath) {

		if (currentPagePath.contains(sitesRootPath)) {
			String countryLocalePath = TrainingHelper.getRelativePath(currentPagePath, resource);
			headerPath = getExpFragmentPathDetails(currentPagePath, countryLocalePath, headerPath,
					TrainingSiteConfigurationUtils.getHeaderExpFragmentName());
			footerPath = getFooterExpFragmentPathDetails(currentPagePath, countryLocalePath, footerPath,
					TrainingSiteConfigurationUtils.getFooterExpFragmentName(),
					TrainingSiteConfigurationUtils.getGlobalfooterExpFragmentName());
			gameLandingGridPath = getExpFragmentPathDetails(currentPagePath, countryLocalePath, gameLandingGridPath,
					TrainingSiteConfigurationUtils.getGameLandingExpFragmentName());

			retailerInterstitialPath = getExpFragmentPathDetails(currentPagePath, countryLocalePath,
					retailerInterstitialPath, TrainingSiteConfigurationUtils.getRetailerInterstitialPath());

			leavingInterstitialPath = getExpFragmentPathDetails(currentPagePath, countryLocalePath,
					leavingInterstitialPath, TrainingSiteConfigurationUtils.getLeavingInterstitialPath());

			interstitialApp = getExpFragmentPathDetails(currentPagePath, countryLocalePath, interstitialApp,
					TrainingSiteConfigurationUtils.getInterstitialApp());

			characterCategoryFilterPath = getExpFragmentPathDetails(currentPagePath, countryLocalePath,
					characterCategoryFilterPath, TrainingSiteConfigurationUtils.getCategoryFilterExpFragmentName());

			productThumbnailGridFragPath = getExpFragmentPathDetails(currentPagePath, countryLocalePath,
					productThumbnailGridFragPath,
					TrainingSiteConfigurationUtils.getProductThumbnailGridExpFragmentName());

			productCategoryFilterPath = getExpFragmentPathDetails(currentPagePath, countryLocalePath,
					productCategoryFilterPath,
					TrainingSiteConfigurationUtils.getProductCategoryFilterExpFragmentName());
			productDetailGridTitleFragPath = getExpFragmentPathDetails(currentPagePath, countryLocalePath,
					productDetailGridTitleFragPath,
					TrainingSiteConfigurationUtils.getProductDetailTitleExpFragmentName());

			TrainingSiteConfigurationUtils.getCategoryFilterExpFragmentName();
		}

	}

	private String getExpFragmentPathDetails(String currentPagePath, String countryLocalePath, String path,
			String expFragmentName) {
		if (countryLocalePath.split(Constants.SLASH).length == 3 && !currentPagePath.contains(languageMasters)) {
			if (countryLocalePath.split(Constants.SLASH)[2].contains("-")
					&& countryLocalePath.split(Constants.SLASH)[2].indexOf('-') == 2) {
				String localeString = countryLocalePath.split(Constants.SLASH)[2];
				String locale = localeString.split("-")[0];
				path = TrainingSiteConfigurationUtils.getExpFragmentRootPath() + brandName + Constants.SLASH + locale
						+ Constants.SLASH + expFragmentName + Constants.SLASH + expFragmentName + Constants.UNDERSCORE
						+ countryLocalePath.split(Constants.SLASH)[2] + Constants.JCR_CONTENT_ROOT;

				path = handleNullResource(path, expFragmentName, locale);
			}
		} else if (countryLocalePath.split(Constants.SLASH).length == 3 && currentPagePath.contains(languageMasters)) {
			String locale = countryLocalePath.split(Constants.SLASH)[2];
			path = TrainingSiteConfigurationUtils.getExpFragmentRootPath() + brandName + Constants.SLASH + locale
					+ Constants.SLASH + expFragmentName + Constants.SLASH + Constants.MASTER
					+ Constants.JCR_CONTENT_ROOT;

			path = handleNullResource(path, expFragmentName, locale);
		} else {
			path = TrainingSiteConfigurationUtils.getExpFragmentRootPath() + brandName + Constants.SLASH + "en"
					+ Constants.SLASH + expFragmentName + Constants.SLASH + Constants.MASTER
					+ Constants.JCR_CONTENT_ROOT;
		}

		return path;
	}

	/**
	 * @param currentPagePath   current Page Path
	 * @param countryLocalePath country Locale Path
	 * @param path              Experience Fragment Path
	 * @param expFragmentName   Experience Fragment Name
	 * @return path Experience Fragment Path
	 */
	private String getFooterExpFragmentPathDetails(String currentPagePath, String countryLocalePath, String path,
			String expFragmentName, String globalFooterExpFragName) {
		if (countryLocalePath.split(Constants.SLASH).length == 3 && !currentPagePath.contains(languageMasters)) {
			if (countryLocalePath.split(Constants.SLASH)[2].contains("-")
					&& countryLocalePath.split(Constants.SLASH)[2].indexOf('-') == 2) {
				String localeString = countryLocalePath.split(Constants.SLASH)[2];
				String locale = localeString.split("-")[0];
				boolean isBrandExcluded = checkIfBrandisExcluded();
				if ("us".equals(countryLocalePath.split(Constants.SLASH)[1]) && !isBrandExcluded) {
					path = TrainingSiteConfigurationUtils.getExpFragmentRootPath() + globalFooterExpFragName
							+ Constants.SLASH + Constants.MASTER + Constants.JCR_CONTENT_ROOT;
				} else {
					path = TrainingSiteConfigurationUtils.getExpFragmentRootPath() + brandName + Constants.SLASH
							+ locale + Constants.SLASH + expFragmentName + Constants.SLASH + expFragmentName
							+ Constants.UNDERSCORE + countryLocalePath.split(Constants.SLASH)[2]
							+ Constants.JCR_CONTENT_ROOT;
				}
				path = handleNullResource(path, expFragmentName, locale);
			}
		} else if (countryLocalePath.split(Constants.SLASH).length == 3 && currentPagePath.contains(languageMasters)) {
			String locale = countryLocalePath.split(Constants.SLASH)[2];
			path = TrainingSiteConfigurationUtils.getExpFragmentRootPath() + brandName + Constants.SLASH + locale
					+ Constants.SLASH + expFragmentName + Constants.SLASH + Constants.MASTER
					+ Constants.JCR_CONTENT_ROOT;
			path = handleNullResource(path, expFragmentName, locale);
		} else {
			path = TrainingSiteConfigurationUtils.getExpFragmentRootPath() + brandName + Constants.SLASH + "en"
					+ Constants.SLASH + expFragmentName + Constants.SLASH + Constants.MASTER
					+ Constants.JCR_CONTENT_ROOT;
		}

		return path;
	}

	private boolean checkIfBrandisExcluded() {
		String[] excludedBrands = TrainingSiteConfigurationUtils.getExcludedBrandsFooter();
		if (null != excludedBrands && excludedBrands.length > 0) {
			for (String brand : excludedBrands) {
				if (brand.equalsIgnoreCase(brandName)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * check and handle null resource condition
	 * 
	 * @param path            Experience Fragment Path
	 * @param expFragmentName Experience Fragment Name
	 * @return path Experience Fragment Path
	 */
	private String handleNullResource(String path, String expFragmentName, String locale) {
		if (null == resource.getResourceResolver().getResource(path)) {
			path = TrainingSiteConfigurationUtils.getExpFragmentRootPath() + brandName + Constants.SLASH + locale
					+ Constants.SLASH + expFragmentName + Constants.SLASH + Constants.MASTER
					+ Constants.JCR_CONTENT_ROOT;
			if (null == resource.getResourceResolver().getResource(path)) {
				path = TrainingSiteConfigurationUtils.getExpFragmentRootPath() + brandName + Constants.SLASH + "en"
						+ Constants.SLASH + expFragmentName + Constants.SLASH + Constants.MASTER
						+ Constants.JCR_CONTENT_ROOT;
			}
		}
		return path;
	}

	private String getHomePageTitle(Resource resource, boolean childPageFlag) {
		String homePagetitle = null;
		Resource homePageResource = resource.getResourceResolver()
				.getResource(homePagePath + Constants.SLASH_JCR_CONTENT);
		if (homePageResource != null) {
			ValueMap properties = homePageResource.adaptTo(ValueMap.class);
			if (properties != null) {
				if (childPageFlag) {

					homePagetitle = properties.get("globalSeoTitle", "");

				} else {
					homePagetitle = properties.get("seoTitle", "");
				}
			}
		}

		return homePagetitle;
	}

	private boolean isHomepage(String path) {

		return path.equalsIgnoreCase(homePagePath);
	}

	private void getHrefLangPropertyList() {
		String currentResPath = resource.getPath();
		String currentpagePath = currentResPath.replace("jcr:content", "");
		String countryLocale = TrainingHelper.getRelativePath(currentpagePath, resource);
		if (StringUtils.isNotBlank(countryLocale)) {
			String tempPath = currentpagePath.substring(currentpagePath.indexOf(countryLocale),
					currentpagePath.length() - 1);
			String relativePath = tempPath.replace(countryLocale, "");
			String sitesRootPath = PropertyReaderUtils.getTrainingPath() + brandName;
			Session session = resource.getResourceResolver().adaptTo(Session.class);
			QueryBuilder queryBuilder = resource.getResourceResolver().adaptTo(QueryBuilder.class);
			SearchResult result = TrainingHelper.getCountryNodesByLanguage(sitesRootPath, session, queryBuilder);
			if (null != result) {
				try {
					for (Hit hit : result.getHits()) {
						if (null != hit.getPath()) {
							PageManager pgmgr = resource.getResourceResolver().adaptTo(PageManager.class);
							if (null != pgmgr) {
								String pagepath = hit.getPath().replace(Constants.SLASH_JCR_CONTENT, "");
								boolean enableHrefLang = checkHrefLangEligibility(pagepath, pgmgr, relativePath);
								if (enableHrefLang) {
									HrefLangPojo hrefLangPojo = new HrefLangPojo();
									hrefLangPojo.setUrl(pagepath + relativePath);
									hrefLangPojo.setLocale(TrainingHelper.getPageLocale(pagepath));
									hrefLangList.add(hrefLangPojo);
								}
							}
						}
					}
					Iterator<Resource> resources = result.getResources();
					if (resources.hasNext()) {
						ResourceResolver leakingResResolver = resources.next().getResourceResolver();
						if (leakingResResolver.isLive()) {
							leakingResResolver.close();
						}
					}
				} catch (RepositoryException e) {
					LOGGER.error("RepositoryException occured while retriving hreflang page list {} ", e);
				}
			}
		}

	}

	private boolean checkHrefLangEligibility(String pagepath, PageManager pgmgr, String relativePath) {
		boolean isHreflangUrl = true;
		if (null != pgmgr && null != pgmgr.getPage(pagepath)) {
			Page currentPage = pgmgr.getPage(pagepath);
			Page localepage = pgmgr.getPage(pagepath + relativePath);
			if (null != currentPage) {
				String homePPath = pagepath + slashHome;
				Page homepage = pgmgr.getPage(homePPath);
				if (null != homepage && null != homepage.getProperties()) {
					ValueMap map = homepage.getProperties();
					if (map.containsKey(cqRedirectTarget) && null != map.get(cqRedirectTarget)) {

						isHreflangUrl = false;
					}
				}
			}
			if (null == localepage) {
				isHreflangUrl = false;
			}
		}
		return isHreflangUrl;
	}

	/**
	 * Method to fetch the Page Title and SEO Properties
	 */
	private void fetchTitleAndSeoDetails() {
		PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
		if (pageManager != null) {
			Page currentPage = pageManager.getContainingPage(resource);
			if (null != currentPage && currentPage.getAbsoluteParent(5 + leftIndexShiftForSiteWOParentName) != null) {
				Page homePage = currentPage.getAbsoluteParent(5 + leftIndexShiftForSiteWOParentName);
				homePagePath = homePage.getPath();
				fetchtitleDetails(currentPage);
				Resource seoKeywordsNodeResource = resource.getResourceResolver()
						.getResource(resource.getPath() + "/metaKeywords");
				fetchSeoDetails(seoKeywordsNodeResource);

			}
		}
	}

	private void getPageNameAndParentPageType(Resource resource) {
		PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
		if (null != pageManager) {
			Page currentPage = pageManager.getContainingPage(resource);
			if (null != currentPage && null != currentPage.getAbsoluteParent(5 + leftIndexShiftForSiteWOParentName)) {
				currentPagePath = currentPage.getPath();
				Page homePage = currentPage.getAbsoluteParent(5 + leftIndexShiftForSiteWOParentName);
				homePagePath = homePage.getPath();
				getParentPageTypeAndPageName(currentPage, homePage, currentPagePath);
			}
		}
	}

	/**
	 * @param currentPage
	 * @param homePage
	 */
	private void getParentPageTypeAndPageName(Page currentPage, Page homePage, String currentPagePath) {
		if (StringUtils.isNotBlank(currentPagePath) && StringUtils.isNotBlank(homePagePath)
				&& currentPagePath.equalsIgnoreCase(homePagePath)) {
			pageName = currentPage.getName();
			parentPageType = currentPage.getName();
		} else if (currentPagePath.contains(TrainingSiteConfigurationUtils.getFpRootPath())
				&& !currentPagePath.contains("rescue-heroes")) {
			fetchRescuepageTypenName(currentPage, currentPagePath);
		} else {
			pageName = currentPage.getName();
			Iterator<Page> rootPageIterator = homePage.listChildren();
			while (rootPageIterator.hasNext()) {
				Page childPage = rootPageIterator.next();
				if (pageName.equals(childPage.getName())) {
					parentPageType = pageName;
					break;
				} else {
					parentPageType = currentPage.getParent().getName();
				}
			}
		}
	}

	private void fetchRescuepageTypenName(Page currentPage, String currentPagePath) {
		StringBuilder tempName = new StringBuilder();
		tempName.append(TrainingHelper.getBrandName(resource) + ":" + fetchSiteCountry(currentPagePath));
		int pageDepth = currentPage.getDepth();
		int absoluteHome = 4;
		if (currentPage.getPath().equals(currentPage.getAbsoluteParent(absoluteHome).getPath())) {
			parentPageType = "corporate page";
		} else {
			parentPageType = "category index";
		}
		Page tempPage;
		while (absoluteHome < pageDepth) {

			tempPage = currentPage.getAbsoluteParent(absoluteHome);
			tempName.append(":" + tempPage.getName());
			absoluteHome++;

		}
		pageName = tempName.toString();

	}

	private void fetchtitleDetails(Page currentPage) {

		boolean ishomePage = isHomepage(currentPage.getPath());

		if (ishomePage) {

			if (getHomePageTitle(resource, true) != null && getHomePageTitle(resource, false) != null) {
				title = getHomePageTitle(resource, true) + " : " + getHomePageTitle(resource, false);
			} else if (getHomePageTitle(resource, true) == null) {
				title = getHomePageTitle(resource, false);
			} else if (getHomePageTitle(resource, false) == null) {
				title = getHomePageTitle(resource, true);
			}

		} else {

			ValueMap prop = resource.adaptTo(ValueMap.class);
			String currentPageTitle = null;
			if (prop != null) {
				currentPageTitle = prop.get("seoTitle", "");
			}
			String homePageTitle = getHomePageTitle(resource, true);

			if (homePageTitle != null && currentPageTitle != null) {

				title = currentPageTitle + " : " + homePageTitle;

			} else if (homePageTitle == null) {
				title = currentPageTitle;
			} else {
				title = homePageTitle;
			}
		}
	}

	private void fetchSeoDetails(Resource seoKeywordsNodeResource) {
		List<String> keywordsList = new ArrayList<>();
		Node seoNode = null;
		Map<String, ValueMap> seoLinkedHashMap = new HashMap<>();
		if (seoKeywordsNodeResource != null) {
			seoNode = seoKeywordsNodeResource.adaptTo(Node.class);
			if (null != seoNode) {
				seoLinkedHashMap = multifieldReader.propertyReader(seoNode);
			}
			for (Map.Entry<String, ValueMap> mapEntry : seoLinkedHashMap.entrySet()) {
				keywordsList.add(mapEntry.getValue().get("keyword").toString());
			}

			keywordCommaSeparated = String.join(",", keywordsList);

		}
	}

	private String fetchSiteCountry(String currentPagePath) {
		String tempPath = TrainingHelper.getRelativePath(currentPagePath, resource);
		if (!tempPath.isEmpty()) {
			siteCountry = tempPath.substring(tempPath.indexOf('/') + 1, tempPath.lastIndexOf('/'));
		}
		return siteCountry;
	}

	public String getPropertiesJson() {
		return propertiesJson;
	}

	public String getTitle() {
		return title;
	}

	public String getScriptUrl() {
		return scriptUrl;
	}

	public String getKeywordCommaSeparated() {
		return keywordCommaSeparated;
	}

	public List<HrefLangPojo> getHrefLangList() {
		return hrefLangList;
	}

	public String getClientlibCategory() {
		return clientlibCategory;
	}

	public String getGameLandingGridPath() {
		return gameLandingGridPath;
	}

	public String getCharacterCategoryFilterPath() {
		return characterCategoryFilterPath;
	}

	public String getHeaderPath() {
		return headerPath;
	}

	public String getFooterPath() {
		return footerPath;
	}

	public String getRetailerInterstitialPath() {
		return retailerInterstitialPath;
	}

	public String getProductThumbnailGridFragPath() {
		return productThumbnailGridFragPath;
	}

	public void setProductThumbnailGridFragPath(String productThumbnailGridFragPath) {
		this.productThumbnailGridFragPath = productThumbnailGridFragPath;
	}

	public String getProductCategoryFilterPath() {
		return productCategoryFilterPath;
	}

	public void setProductCategoryFilterPath(String productCategoryFilterPath) {
		this.productCategoryFilterPath = productCategoryFilterPath;
	}

	public String getProductDetailGridTitleFragPath() {
		return productDetailGridTitleFragPath;
	}

	public void setProductDetailGridTitleFragPath(String productDetailGridTitleFragPath) {
		this.productDetailGridTitleFragPath = productDetailGridTitleFragPath;
	}

	public String getBusinessSiteName() {
		return businessSiteName;
	}

	public String getSiteCountry() {
		return siteCountry;
	}

	public String getLeavingInterstitialPath() {
		return leavingInterstitialPath;
	}

	public String getInterstitialApp() {
		return interstitialApp;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setHeaderPath(String headerPath) {
		this.headerPath = headerPath;
	}

	public void setHomePagePath(String homePagePath) {
		this.homePagePath = homePagePath;
	}

	public String getParentPageType() {
		return parentPageType;
	}

	public String getPageName() {
		return pageName;
	}

	public String getRescueBrandName() {
		return rescueBrandName;
	}

	public String getRescueParentName() {
		return rescueParentName;
	}

	public String getPageLocale() {
		return pageLocale;
	}

	public String getAdobeTrackingNameForPage() {
		return adobeTrackingNameForPage;
	}

	public String getHomePagePath() {
		return homePagePath;
	}

	public String getExpFrLocaleRH() {
		return TrainingHelper.getExpFrLocaleRH(resource.getPath());
	}

	public String getIfSiteisMattelTraining() {
		if (TrainingHelper.checkIfSiteisTraining(resource.getPath())) {
			return "true";
		} else {
			return "false";
		}
	}
}
