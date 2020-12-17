package com.training.core.helper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.training.core.constants.Constants;
import com.training.core.utils.PropertyReaderUtils;
import com.training.core.utils.TrainingSiteConfigurationUtils;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
public class TrainingHelper extends WCMUsePojo {

	private static final Logger LOGGER = LoggerFactory.getLogger(TrainingHelper.class);
	private String pathURL;

	@Override
	public void activate() throws Exception {
		String text = get("text", String.class);
		Resource res = getResource();
		pathURL = checkLink(text, res);
	}

	public static String checkLink(String text, Resource resource) {
		ResourceResolver resolver = null;
		if (null != resource) {
			resolver = resource.getResourceResolver();
		}
		if (text != null) {
			if (text.startsWith("/content") && !text.startsWith("/content/dam") && !text.contains(".html")) {
				if (text.contains("#")) {
					String[] tempURL = text.split("#");
					if (tempURL.length > 1) {
						String url = tempURL[0];
						String param = tempURL[1];
						text = checkResolverMapping(url, resolver);
						if (StringUtils.isNotBlank(param)) {
							text = text + "#" + param;
						}
					}
				} else {
					text = checkResolverMapping(text, resolver);
				}
				return text;
			} else {
				return text;
			}
		}
		return text;
	}

	public static String checkResolverMapping(String text, ResourceResolver resolver) {
		if (null != resolver) {
			text = resolver.map(text);
		}
		if (text.startsWith("/content")) {
			text = text + ".html";
		}
		return text;
	}

	public static String getBrandName(String path) {
		LOGGER.info("getBrandName method of TrainingHelper starts");
		String trainingPath = PropertyReaderUtils.getTrainingPath();
		String fpPath = TrainingSiteConfigurationUtils.getFpRootPath();
		String trainingExpFragmentPath = TrainingSiteConfigurationUtils.getExpFragmentRootPath();
		String fpExpFragmentPath = TrainingSiteConfigurationUtils.getFpExpFragmentRootPath();
		String rescueExpFragmentPath = TrainingSiteConfigurationUtils.getRescueExpFgmtRootPath();
		if (path.contains(trainingPath) && !path.equalsIgnoreCase(trainingPath + Constants.JCR_CONTENT)) {
			return fetchSubstring(path, trainingPath);
		} else if (path.contains(trainingExpFragmentPath)) {
			return fetchSubstring(path, trainingExpFragmentPath);
		} else if (path.contains(fpExpFragmentPath) && !path.contains(rescueExpFragmentPath) || path.contains(fpPath)) {
			return getFPBrandName(path);
		}
		LOGGER.info("getBrandName method of TrainingHelper end");
		return "";
	}

	public static String getBrandName(Resource resource) {
		LOGGER.info("getBrandName method of TrainingHelper starts");
		String path = resource.getPath();
		int index = path.indexOf("/jcr:content");
		path = path.substring(0, index);
		if (!path.contains("experience-fragments")) {
			PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
			if (pageManager.getPage(path) != null) {
				Page currentPage = pageManager.getPage(path);
				if (currentPage.getAbsoluteParent(2).hasChild("language-masters")) {
					return currentPage.getAbsoluteParent(2).getName();
				} else if (currentPage.getAbsoluteParent(1).hasChild("language-masters")) {
					return currentPage.getAbsoluteParent(1).getName();
				}
			}
		} else if (path.contains("experience-fragments")) {
			String[] pathArray = path.split("/");
			return pathArray[3];
		} else {
			return "";
		}
		LOGGER.info("getBrandName method of TrainingHelper end");
		return "";
	}

	@SuppressWarnings("unused")
	private static String getFPBrandName(String path) {
		LOGGER.info("getFPBrandName method of TrainingHelper starts");
		String fpRootPath = TrainingSiteConfigurationUtils.getFpRootPath();
		String fpExpFragmentPath = TrainingSiteConfigurationUtils.getFpExpFragmentRootPath();
		if (path.contains(fpRootPath) && !path.equalsIgnoreCase(fpRootPath + Constants.JCR_CONTENT)) {
			return fetchSubstring(path, fpRootPath);
		} else if (path.contains(fpExpFragmentPath)) {
			return fetchSubstring(path, fpExpFragmentPath);
		}
		LOGGER.info("getFPBrandName method of TrainingHelper end");
		return "";
	}

	private static String fetchSubstring(String path, String startPath) {
		int start = startPath.length();
		int end = path.indexOf('/', start);
		return path.substring(start, end);
	}

	public static String getAssetMetadataValue(String imagePath, ResourceResolver requestResolver,
			String propertyName) {
		String propertyValue = "";
		if (StringUtils.isNotBlank(imagePath)) {
			Resource imageRes = requestResolver.getResource(imagePath);
			if (null != imageRes) {
				Asset imageAsset = imageRes.adaptTo(Asset.class);
				if (null != imageAsset) {
					propertyValue = null != imageAsset.getMetadataValue(propertyName)
							? imageAsset.getMetadataValue(propertyName)
							: "";
				}

			}
		}
		return propertyValue;
	}

	public static String getSlideCount(String slideCount, String[] slideshowValueMapping, String brand) {
		if (null != slideshowValueMapping && slideshowValueMapping.length > 0 && null != brand) {
			for (String mapping : slideshowValueMapping) {
				if (mapping.contains(":") && mapping.split(":").length > 1) {
					String brandName = mapping.split(":")[0];
					String count = mapping.split(":")[1];
					if (brand.equals(brandName)) {
						slideCount = count;
					}
				}
			}
		}
		return slideCount;
	}

	public static String getRelativePath(String currentPagePath, Resource resource) {
		LOGGER.info("getRelativePath method of TrainingHelper starts");

		String sitesRootPath = StringUtils.EMPTY;
		String brandName;
		if (TrainingHelper.checkIfSiteisTraining(resource.getPath())) {
			brandName = TrainingHelper.getBrandName(resource.getPath());
		} else {
			brandName = TrainingHelper.getBrandName(resource);
		}
		if (StringUtils.isNotBlank(brandName) && currentPagePath.contains(PropertyReaderUtils.getTrainingPath())) {
			sitesRootPath = PropertyReaderUtils.getTrainingPath() + brandName;
		} else {
			int indexBrandName = currentPagePath.indexOf(brandName);
			sitesRootPath = currentPagePath.substring(0, indexBrandName + brandName.length());
		}

		LOGGER.debug("sitesRootPath value of TrainingHelper is {}", sitesRootPath);
		String relativePath = "";
		if (null != resource) {
			PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
			if (null != pageManager) {
				Page currentPage = pageManager.getContainingPage(resource);
				String homePageName = currentPage.getAbsoluteParent(5 + leftIndexShiftForSiteWOParentName(resource))
						.getName();
				relativePath = currentPagePath.substring(currentPagePath.indexOf(Constants.SLASH + homePageName),
						currentPagePath.length() - 1);
			}
		}
		String tempString = currentPagePath.replace(sitesRootPath, "");
		LOGGER.debug("tempString value of TrainingHelper is {}", tempString);

		String countryLocalePath = "";
		if (StringUtils.isNotBlank(relativePath)) {
			countryLocalePath = tempString.substring(0, tempString.indexOf(relativePath));
		}
		LOGGER.debug("countryLocalePath value of TrainingHelper is {}", countryLocalePath);
		LOGGER.info("getRelativePath method of TrainingHelper end");
		return countryLocalePath;

	}

	public static String getPageLocale(String pagePath) {
		LOGGER.info("getPageLocale method of TrainingHelper starts");
		String locale = "";
		String[] pagePathArray = pagePath.split("/");
		for (String pagepath : pagePathArray) {
			if (pagepath.contains("-") && pagepath.indexOf('-') == 2) {
				locale = pagepath;
			}
		}
		LOGGER.info("getPageLocale method of TrainingHelper end");
		return locale;
	}

	public static String getExpFrLocaleRH(String pagePath) {
		LOGGER.info("getExpFrLocaleRH method of TrainingHelper starts");
		String locale = "";
		if (pagePath.contains("rescue-heroes") && pagePath.contains("experience-fragments")) {
			String[] pagePathArray = pagePath.split("/");
			for (String pagepath : pagePathArray) {
				if (pagepath.contains("_") && pagepath.contains("-")
						&& (pagepath.indexOf('-') - pagepath.indexOf('_')) == 3) {
					locale = pagepath.substring(pagepath.indexOf('_') + 1);
				}
			}
		}
		LOGGER.info("getExpFrLocaleRH method of TrainingHelper end");
		return locale;
	}

	public static SearchResult getCountryNodesByLanguage(String sourcePath, Session session,
			QueryBuilder queryBuilder) {
		LOGGER.info("getCountryNodesByLanguage method of TrainingHelper starts");
		Map<String, String> querrymap = new HashMap<>();
		querrymap.put("path", sourcePath);
		querrymap.put("type", "cq:PageContent");
		querrymap.put("property", "jcr:language");
		querrymap.put("property.operation", "exists");
		querrymap.put("p.nodedepth", "2");
		querrymap.put("p.hits", "full");
		querrymap.put("p.limit", "-1");
		Query pageQuery = queryBuilder.createQuery(PredicateGroup.create(querrymap), session);
		LOGGER.info("getCountryNodesByLanguage method of TrainingHelper end");
		return null != pageQuery ? pageQuery.getResult() : null;
	}

	public String getPathURL() {
		return pathURL;
	}

	public static String checkProductThumbnailExpFragemnt(ResourceResolver resolver, Resource currPageRootRes) {

		LOGGER.info("checkProductThumbnailExpFragemnt method of TrainingHelper starts");
		String productThumbnailGridNode = "";
		String expFragmentResource = "cq/experience-fragments/editor/components/experiencefragment";
		String slingResourceType = "sling:resourceType";
		String frgamentPath = "fragmentPath";
		if (null != currPageRootRes) {
			Node currPageRootNode = currPageRootRes.adaptTo(Node.class);
			if (null != currPageRootNode) {
				try {
					NodeIterator iter = currPageRootNode.getNodes();
					if (null != iter) {
						productThumbnailGridNode = fetchProductThumbnailExpFragment(iter, slingResourceType,
								expFragmentResource, frgamentPath, resolver);
					}

				} catch (RepositoryException e) {
					LOGGER.error("RepositoryException Occured {} ", e);
				}
			}
		}
		LOGGER.info("checkProductThumbnailExpFragemnt method of TrainingHelper end");
		return productThumbnailGridNode;
	}

	private static String fetchProductThumbnailExpFragment(NodeIterator iter, String slingResourceType,
			String expFragmentResource, String frgamentPath, ResourceResolver resolver) throws RepositoryException {
		LOGGER.info("fetchProductThumbnailExpFragment method of TrainingHelper starts");
		String productThumbnailGridNode = "";
		while (iter.hasNext()) {
			Node node = iter.nextNode();
			if (node.hasProperty(slingResourceType) && null != node.getProperty(slingResourceType)) {
				String resType = node.getProperty(slingResourceType).getValue().toString();
				if (expFragmentResource.equalsIgnoreCase(resType)) {
					String fragmentPath = node.hasProperty(frgamentPath)
							? node.getProperty(frgamentPath).getValue().toString()
							: "";
					LOGGER.debug("fragmentPath value of TrainingHelper is {}", fragmentPath);
					if (StringUtils.isNotBlank(fragmentPath)) {
						Resource expFragRes = resolver.getResource(fragmentPath + "/jcr:content/root");
						productThumbnailGridNode = checkProductThumbnailGridPath(expFragRes, productThumbnailGridNode);
					}
				}
			}
		}
		LOGGER.info("fetchProductThumbnailExpFragment method of TrainingHelper end");
		return productThumbnailGridNode;
	}

	private static String checkProductThumbnailGridPath(Resource expFragRes, String productThumbnailGridNode)
			throws RepositoryException {
		LOGGER.info("checkProductThumbnailGridPath method of TrainingHelper starts");
		if (null != expFragRes) {
			Node expFragNode = expFragRes.adaptTo(Node.class);
			if (null != expFragNode && expFragNode.hasNode("productthumbnailgrid")) {
				productThumbnailGridNode = expFragNode.getNode("productthumbnailgrid").getPath();
			}
		}
		LOGGER.info("checkProductThumbnailGridPath method of TrainingHelper end");
		return productThumbnailGridNode;

	}

	public static String checkForProperty(Node node, String property) throws RepositoryException {
		return node.hasProperty(property) ? node.getProperty(property).getString() : "";

	}

	public static String checkPropertyObject(Object value) {
		return value != null ? value.toString() : "";
	}

	public static String checkForProperty(Asset videoAsset, String property) {
		return videoAsset.getMetadata(property) != null ? videoAsset.getMetadataValue(property) : "";
	}

	public static Boolean checkBooleanProperty(Page page, String property, Boolean defaultBoolean) {
		return page.getProperties().get(property, Boolean.class) != null
				? page.getProperties().get(property, Boolean.class)
				: defaultBoolean;
	}

	public static String checkForProperty(Page page, String property) {
		return page.getProperties("navThumbAlt") != null ? page.getProperties().get(property, String.class) : "";
	}

	public static String fetchLocaleFromDam(String path, Resource resource) {
		String damLocale = "";
		if (path != null) {
			String brandName;
			if (TrainingHelper.checkIfSiteisTraining(resource.getPath())) {
				brandName = TrainingHelper.getBrandName(resource.getPath());
			} else {
				brandName = TrainingHelper.getBrandName(resource);
			}
			String replaceText = PropertyReaderUtils.getTrainingDamPath() + brandName
					+ PropertyReaderUtils.getVideoDamPath();
			path = path.replace(replaceText, "");
			damLocale = path.split("/")[0];
		}
		return damLocale;

	}

	public static String getHomePagePath(Resource resource) {
		String homePagePath = null;
		PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
		Page page = null;
		if (pageManager != null) {
			page = pageManager.getContainingPage(resource);
		}
		if (page != null) {
			homePagePath = page.getAbsoluteParent(5 + leftIndexShiftForSiteWOParentName(resource)).getPath();
			LOGGER.debug("homePagePath is - {}", homePagePath);
		}
		return homePagePath;
	}

	public static void getLeakingResResolver(SearchResult result) {
		Iterator<Resource> resources = result.getResources();
		if (resources.hasNext()) {
			ResourceResolver leakingResResolver = resources.next().getResourceResolver();
			if (leakingResResolver.isLive()) {
				leakingResResolver.close();
			}
		}
	}

	public static String getValueMapNodeVale(ValueMap nodeValues, String properties) {
		return nodeValues.containsKey(properties) ? nodeValues.get(properties, String.class) : "";
	}

	public static Boolean isNullOrEmpty(String[] navigationalLinks) {
		return navigationalLinks == null || navigationalLinks.length < 1;
	}

	public static boolean checkIfSiteisTraining(String path) {
		if (path.contains(PropertyReaderUtils.getTrainingPath())
				|| path.contains(TrainingSiteConfigurationUtils.getExpFragmentRootPath()))
			return true;
		return false;
	}

	public static int leftIndexShiftForSiteWOParentName(Resource resource) {
		String path = resource.getPath();
		int index = path.indexOf("/jcr:content");
		path = path.substring(0, index);
		if (!path.contains("experience-fragments")) {
			PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
			if (pageManager.getPage(path) != null) {
				Page currentPage = pageManager.getPage(path);
				if (currentPage.getAbsoluteParent(1).hasChild("language-masters")) {
					return -1;
				}
			}
		}
		return 0;
	}

}
