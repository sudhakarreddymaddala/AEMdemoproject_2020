package com.training.core.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.training.core.constants.Constants;
import com.training.core.helper.TrainingHelper;
import com.training.core.pojos.VideoCategory;
import com.training.core.pojos.VideoTile;
import com.training.core.utils.PropertyReaderUtils;
import com.training.core.utils.TrainingSiteConfigurationUtils;
import com.training.core.utils.VideosDamLanguageMapping;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Component(service = VideoGalleryService.class, immediate = true)
@Designate(ocd = VideoGalleryService.Config.class)
public class VideoGalleryService {

	private static final Logger LOGGER = LoggerFactory.getLogger(VideoGalleryService.class);
	ResourceResolver resolver;

	@Reference
	QueryBuilder queryBuilder;

	@Reference
	ResourceResolverFactory resolverFactory;

	List<VideoTile> masterList;
	String manual = "manual";
	String automatic = "automatic";
	String service = "readwriteservice";
	String serviceUser = "playserviceuser";

	/**
	 * @param videoGalleryNodepath * @return
	 * 
	 *                             Method used for Manual and Landing option in
	 *                             Gallery.
	 */
	public List<VideoTile> getVideosForManual(String videoGalleryNodepath) {
		LOGGER.info("getVideosForManual -> Start");
		List<VideoTile> videoNodeList1 = new ArrayList<>();
		List<VideoTile> videoNodeList2 = new ArrayList<>();
		List<VideoTile> videoNodeList3 = new ArrayList<>();
		List<VideoTile> videoNodeList4 = new ArrayList<>();
		List<VideoTile> filteredList = new ArrayList<>();

		Map<String, Object> map = new HashMap<>();
		map.put(ResourceResolverFactory.SUBSERVICE, service);
		map.put(ResourceResolverFactory.USER, serviceUser);
		try {
			if (resolverFactory != null) {
				resolver = resolverFactory.getServiceResourceResolver(map);
			}
			Resource resource = resolver.getResource(videoGalleryNodepath);
			if (resource != null) {
				String videoColumn1Path = videoGalleryNodepath + "/videoGalleryColumn1";
				String videoColumn2Path = videoGalleryNodepath + "/videoGalleryColumn2";
				String videoColumn3Path = videoGalleryNodepath + "/videoGalleryColumn3";
				String videoColumn4Path = videoGalleryNodepath + "/videoGalleryColumn4";

				videoNodeList1 = buildNodeList(videoColumn1Path, resolver);
				videoNodeList2 = buildNodeList(videoColumn2Path, resolver);
				videoNodeList3 = buildNodeList(videoColumn3Path, resolver);
				videoNodeList4 = buildNodeList(videoColumn4Path, resolver);
				filteredList = buildMasterList(videoNodeList1, videoNodeList2, videoNodeList3, videoNodeList4);
			}
		} catch (Exception e) {
			LOGGER.debug("Exception Occured {} ", e);
		} finally {
			if (null != resolver && resolver.isLive()) {
				resolver.close();
			}
		}
		LOGGER.info("getVideosForManual -> End");
		return filteredList;
	}

	public void setResolver(ResourceResolver resolver) {
		this.resolver = resolver;
	}

	public void setQueryBuilder(QueryBuilder queryBuilder) {
		this.queryBuilder = queryBuilder;
	}

	public void setResolverFactory(ResourceResolverFactory resolverFactory) {
		this.resolverFactory = resolverFactory;
	}

	/**
	 * Method to fetch video details when we are in automatic mode and by date in
	 * Gallery
	 * 
	 * @param path
	 * @param selectedTags
	 * @param requestResolver
	 * @return
	 */
	public List<VideoTile> getVideosByDate(String path, String selectedTags, boolean isGallery) {
		List<VideoTile> filteredList = new ArrayList<>();
		LOGGER.info("getVideosByDate -> Start");
		Session session = null;
		Map<String, Object> map = new HashMap<>();
		map.put(ResourceResolverFactory.SUBSERVICE, service);
		map.put(ResourceResolverFactory.USER, serviceUser);
		if (resolverFactory != null) {
			try {
				resolver = resolverFactory.getServiceResourceResolver(map);
				session = resolver.adaptTo(Session.class);
				String orderbyProp = TrainingSiteConfigurationUtils.getVideoSortOrderProperty();
				Map<String, String> querrymap = getVideosQueryMap(path, selectedTags, isGallery, orderbyProp);
				Query pageQuery = queryBuilder.createQuery(PredicateGroup.create(querrymap), session);
				if (null != pageQuery) {
					SearchResult result = pageQuery.getResult();
					filteredList = fetchVideoAssetResult(result, resolver);
				}
			} catch (LoginException | RepositoryException e) {
				LOGGER.error("LoginException | RepositoryException Exception {} ", e);
			} finally {
				if (null != resolver && resolver.isLive()) {
					resolver.close();
				}
			}
		}
		LOGGER.info("getVideosByDate -> End");
		return filteredList;
	}

	/**
	 * @param path
	 * @param selectedTags
	 * @param isGallery
	 * @param orderbyProp
	 * @return
	 */
	private Map<String, String> getVideosQueryMap(String path, String selectedTags, boolean isGallery,
			String orderbyProp) {
		Map<String, String> querrymap = new HashMap<>();
		querrymap.put("path", path);
		querrymap.put("type", "dam:Asset");
		if ("dc:videoPublishDate".equalsIgnoreCase(orderbyProp)) {
			querrymap.put("orderby", "@jcr:content/metadata/dc:videoPublishDate");
		} else {
			querrymap.put("orderby", "@jcr:content/jcr:lastModified");
		}
		querrymap.put("orderby.sort", "desc");
		if (isGallery) {
			querrymap.put("p.limit", "12");
		} else {
			querrymap.put("p.limit", "-1");
		}
		if (selectedTags != null) {
			querrymap.put("1_property", "jcr:content/metadata/dc:tags");
			querrymap.put("1_property.value", selectedTags);
		}
		return querrymap;
	}

	/**
	 * Method to fetch the video Asset Details
	 * 
	 * @param result
	 * @return
	 * @throws RepositoryException
	 */
	private List<VideoTile> fetchVideoAssetResult(SearchResult result, ResourceResolver resResolver)
			throws RepositoryException {
		LOGGER.info("fetchVideoAssetResult -> Start");
		List<VideoTile> filteredList = new ArrayList<>();
		if (null != result) {

			for (Hit hit : result.getHits()) {
				Resource resource = resResolver.getResource(hit.getPath());
				if (resource != null) {
					VideoTile videoDetail = prepareVideoTile(resource);
					filteredList.add(videoDetail);
				}
			}
			Iterator<Resource> resources = result.getResources();
			if (resources.hasNext()) {
				ResourceResolver leakingResResolver = resources.next().getResourceResolver();
				if (leakingResResolver.isLive()) {
					leakingResResolver.close();
				}
			}

		}
		LOGGER.info("fetchVideoAssetResult -> End");
		return filteredList;
	}

	/**
	 * @param videoColumnPath
	 * @param resolver
	 * @return
	 * 
	 *         prepare VideoPojo list from Video column node
	 * 
	 */
	private List<VideoTile> buildNodeList(String videoColumnPath, ResourceResolver resolver) {
		LOGGER.info("buildNodeList -> Start");
		List<VideoTile> videoPojoList = new ArrayList<>();
		Resource resource = resolver.getResource(videoColumnPath);
		if (resource != null) {
			Node videoGalleryColumnNode = resource.adaptTo(Node.class);
			try {
				NodeIterator videoGalleryColumnChildren = null;
				if (videoGalleryColumnNode != null) {
					videoGalleryColumnChildren = videoGalleryColumnNode.getNodes();
				}
				if (videoGalleryColumnChildren != null) {
					while (videoGalleryColumnChildren.hasNext()) {
						Node videoTileNode = videoGalleryColumnChildren.nextNode();
						VideoTile videoPojo = buildNode(videoTileNode, resolver);
						if (videoPojo != null) {
							videoPojoList.add(videoPojo);
						}
					}
				}
			} catch (RepositoryException e) {
				LOGGER.error("Repository Exception {} ", e);
			}
		}
		LOGGER.info("buildNodeList -> End");
		return videoPojoList;
	}

	/**
	 * Method to fetch details from Video Tile Node
	 * 
	 * @param videoTileNode
	 * @param resolver
	 * @return
	 */
	private VideoTile buildNode(Node videoTileNode, ResourceResolver resolver) {
		LOGGER.info("start of buildNode");
		VideoTile videoDetails = null;

		try {
			if (videoTileNode != null) {
				String videoThumbnailPath;
				videoThumbnailPath = TrainingHelper.checkForProperty(videoTileNode, "videoThumbnail");
				Resource authorResource = resolver.resolve(videoThumbnailPath);
				videoDetails = prepareVideoTile(authorResource);

			}

		} catch (ValueFormatException e) {
			LOGGER.error("ValueFormatException Exception Occured {} ", e);
		} catch (PathNotFoundException e) {
			LOGGER.error("PathNotFoundException Exception Occured {} ", e);
		} catch (RepositoryException e) {
			LOGGER.error("RepositoryException Exception Occured {} ", e);
		}
		LOGGER.info("end of buildNode");
		return videoDetails;
	}

	/**
	 * @param videoNode
	 * @return
	 * 
	 *         prepare Video Pojo from videoTileNode / Asset
	 */
	public VideoTile prepareVideoTile(Resource assetResource) {
		LOGGER.info("prepareVideoTile -> Start");
		VideoTile videoDetail = null;
		StringBuilder category = new StringBuilder();
		String categories = null;
		try {
			if (null != assetResource) {
				videoDetail = new VideoTile();
				String tag = getAssetMetadataValue(assetResource, "dc:tags");
				if (tag != null) {
					String[] tags = tag.split(",");
					List<String> videoTags = new ArrayList<>();
					for (String vTag : tags) {
						videoTags.add(vTag.trim());
						String[] categorylist = vTag.split("/");
						String tempCategory = "";
						for (int i = 0; i < categorylist.length; i++) {
							tempCategory = categorylist[categorylist.length - 1] + "/";
						}
						category = category.append(tempCategory);
						int len = category.length();
						categories = category.substring(0, len - 1);
					}
					videoDetail.setVideoTags(videoTags);
				}
				videoDetail.setVideoThumbnail(assetResource.getPath());
				videoDetail.setVideoCategory(categories);
				String videotitle = getAssetMetadataValue(assetResource, "dc:title");
				videoDetail.setVideoTitle(videotitle);
				String videoAlt = getAssetMetadataValue(assetResource, "dc:alttext");
				videoDetail.setThumbnailAltTxt(videoAlt);
				String videoDesc = getAssetMetadataValue(assetResource, "dc:description");
				videoDetail.setVideoDesc(videoDesc);
				String ooyalaId = getAssetMetadataValue(assetResource, "dc:ooyalaID");
				videoDetail.setVideoId(ooyalaId);
				String seoUrl = getAssetMetadataValue(assetResource, "dc:seoUrl");
				videoDetail.setSeoUrl(seoUrl);
				String seoTitle = getAssetMetadataValue(assetResource, "dc:seoTitle");
				videoDetail.setSeotitle(seoTitle);
				String metaDesc = getAssetMetadataValue(assetResource, "dc:metaDesc");
				videoDetail.setMetaDesc(metaDesc);
				String keywords = getAssetMetadataValue(assetResource, "dc:metaKeywords");
				videoDetail.setMetaKeywords(keywords);
				String videoName = getAssetMetadataValue(assetResource, "dc:name");
				videoDetail.setVideoName(videoName);
				String alwaysEnglish = getAssetMetadataValue(assetResource, "dc:alwaysEnglish");
				alwaysEnglish = alwaysEnglish.replace("|", "-");
				videoDetail.setAlwaysEnglish(alwaysEnglish);
			}
		} catch (RepositoryException e) {
			LOGGER.error("RepositoryException Occured {} ", e);
		}
		LOGGER.info("prepareVideoTile -> End");
		return videoDetail;
	}

	private String getAssetMetadataValue(Resource assetResource, String metaProperty) throws RepositoryException {
		String propertyvalue = "";
		ResourceResolver resResolver = assetResource.getResourceResolver();
		Resource metadataRes = resResolver.getResource(assetResource, "jcr:content/metadata");
		if (null != metadataRes) {
			Node metadataNode = metadataRes.adaptTo(Node.class);
			if (null != metadataNode && metadataNode.hasProperty(metaProperty)) {
				if (metadataNode.getProperty(metaProperty).isMultiple()) {
					Property property = metadataNode.getProperty(metaProperty);
					Value[] values = property.getValues();
					if (null != values) {
						propertyvalue = Arrays.toString(values).replace("[", "").replace("]", "");
					}
				} else {
					propertyvalue = metadataNode.getProperty(metaProperty).getString();
				}
			}
		}
		return propertyvalue;
	}

	/**
	 * Method for building master list
	 *
	 * @return List of void
	 */
	private List<VideoTile> buildMasterList(List<VideoTile> maxLengthNodesList, List<VideoTile> nodeList2,
			List<VideoTile> nodeList3, List<VideoTile> nodeList4) {
		LOGGER.info("buildMasterList -> Start");
		masterList = new ArrayList<>();
		int count = 0;
		for (int i = 0; i < maxLengthNodesList.size(); i++) {
			if (null != maxLengthNodesList.get(i)) {
				masterList.add(maxLengthNodesList.get(i));
				if (nodeList2.size() > count) {
					masterList.add(nodeList2.get(i));
				}
				if (nodeList3.size() > count) {
					masterList.add(nodeList3.get(i));
				}
				if (!nodeList4.isEmpty() && nodeList4.size() > count) {
					masterList.add(nodeList4.get(i));
				}
			}
			count++;
		}
		LOGGER.info("buildMasterList -> End");
		return masterList;
	}

	@ObjectClassDefinition(name = "Child Page properties")
	public @interface Config {
		@AttributeDefinition(name = "Root path", description = "Please provide the rootpath of retail homepage. Default is /content/play/pollypocket")
		String rootPath() default "/content/play/pollypocket";
	}

	/**
	 * Method to fetch the video List based on the orderVideoBy Parameter
	 * 
	 * @param orderVideoBy
	 * @param videoGalleryNodepath
	 * @param videoAssetPath
	 * @return This Method returns the Videos List based on the order Configuration
	 * @throws RepositoryException
	 * @throws ItemNotFoundException
	 * @throws AccessDeniedException
	 * @throws LoginException
	 */
	public List<VideoTile> getVideoList(String orderVideoBy, String videoGalleryNodepath, String videoAssetPath)
			throws RepositoryException, LoginException {

		LOGGER.info("Start -> getVideoList");
		List<VideoTile> videoList = null;
		String path;
		if (orderVideoBy.equals(manual)) {
			videoList = getVideosForManual(videoGalleryNodepath);
		} else if (orderVideoBy.equals(automatic)) {
			if (StringUtils.isNotEmpty(videoAssetPath)) {
				path = videoAssetPath;
			} else {
				String pageLocale = "";
				if (videoGalleryNodepath.contains("/language-masters/")) {
					pageLocale = getPageLocaleFromMappings(videoGalleryNodepath, pageLocale);
				} else {
					pageLocale = TrainingHelper.getPageLocale(videoGalleryNodepath);
				}
				path = PropertyReaderUtils.getTrainingDamPath() + TrainingHelper.getBrandName(videoGalleryNodepath)
						+ PropertyReaderUtils.getVideoDamPath() + pageLocale + PropertyReaderUtils.getVideoPath();
			}
			videoList = getVideosByDate(path, null, false);
			LOGGER.debug("videoList size {}", videoList.size());
		}
		appendGlobalSeoToLocal(videoGalleryNodepath, videoList);

		LOGGER.info("End -> getVideoList");

		return videoList;
	}

	/**
	 * Method to fetch the Global SEO title from Page
	 * 
	 * @param homePage
	 * @return
	 */
	private String fetchGlobalSeoTitle(Page homePage) {
		String homePageTitle = "";
		if (homePage != null) {
			ValueMap properties = homePage.getProperties();
			if (properties != null) {
				homePageTitle = properties.get("globalSeoTitle", "");
			}
		}
		return homePageTitle;
	}

	/**
	 * Method to Concat Global Seo Title with Current Page SEO Title
	 * 
	 * @param videoGalleryNodepath
	 * @param videoList
	 * @return
	 * @throws RepositoryException
	 * @throws LoginException
	 */
	private List<VideoTile> appendGlobalSeoToLocal(String videoGalleryNodepath, List<VideoTile> videoList)
			throws RepositoryException, LoginException {

		String globalSeoTitle = fetchGlobalSeoDetails(videoGalleryNodepath);
		LOGGER.debug("globalSeoTitle {}", globalSeoTitle);
		for (VideoTile videoTile : videoList) {
			String resultTitle = videoTile.getSeotitle() + " | " + globalSeoTitle;
			videoTile.setSeotitle(resultTitle);

		}

		return videoList;
	}

	/**
	 * Method to Fetch the Global SEO Title Details
	 * 
	 * @param videoGalleryNodepath
	 * @return
	 * @throws LoginException
	 * @throws RepositoryException
	 */
	private String fetchGlobalSeoDetails(String videoGalleryNodepath) throws LoginException, RepositoryException {
		LOGGER.info("Start -> fetchGlobalSeoDetails");
		Map<String, Object> map = new HashMap<>();
		map.put(ResourceResolverFactory.SUBSERVICE, service);
		map.put(ResourceResolverFactory.USER, serviceUser);
		String homePageTitle = "";
		if (resolverFactory != null) {
			resolver = resolverFactory.getServiceResourceResolver(map);
			Resource videoNodeRes = resolver.getResource(videoGalleryNodepath);
			if (videoNodeRes != null) {
				Node videoNode = videoNodeRes.adaptTo(Node.class);
				if (videoNode != null) {
					String homePath = videoNode.getAncestor(6).getPath();
					Resource homePageRes = resolver.getResource(homePath);
					if (homePageRes != null) {
						Page homePage = homePageRes.adaptTo(Page.class);
						homePageTitle = fetchGlobalSeoTitle(homePage);
					}
				}
			}
		}

		if (null != resolver && resolver.isLive()) {
			resolver.close();
		}
		LOGGER.info("end -> fetchGlobalSeoDetails");
		return homePageTitle;

	}

	/**
	 * Method to fetch the Country Locale From Current Page
	 * 
	 * @param videoGalleryNodepath
	 * @param pageLocale
	 * @return
	 */
	public String getPageLocaleFromMappings(String videoGalleryNodepath, String pageLocale) {
		String[] languagemapings = VideosDamLanguageMapping.getLanguageMapping();
		if (null != languagemapings && languagemapings.length > 0) {
			for (String mapping : languagemapings) {
				if (mapping.contains(":") && mapping.split(":").length > 1) {
					String language = mapping.split(":")[0];
					String locale = mapping.split(":")[1];
					if (videoGalleryNodepath.contains("/" + language + "/")) {
						pageLocale = locale;
					}
				}
			}
		}
		return pageLocale;
	}

	/**
	 * Method to fetch the list of Video Categories
	 * 
	 * @param videoNode
	 * @param allCatLabel
	 * @param allCatAnalyticsLabel
	 * @param resolver
	 * @return This Method returns the Video Category List
	 * @throws RepositoryException
	 */
	public List<VideoCategory> getVideoCategoryList(Node videoNode, String allCatLabel, String allCatAnalyticsLabel,
			ResourceResolver resolver) throws RepositoryException {
		LOGGER.info("Start -> getVideoCategoryList");

		boolean checkCategory = videoNode.hasNode("categoryDetail");
		List<VideoCategory> categoryList = new ArrayList<>();
		if (checkCategory) {
			Node categoryNode = videoNode.getNode("categoryDetail");
			if (!allCatLabel.isEmpty()) {
				VideoCategory allLabelPojo = new VideoCategory();
				allLabelPojo.setCategoryTitle(allCatLabel);
				String allCategoryName = allCatLabel.replace(" ", "-");
				if (StringUtils.isNotBlank(allCatAnalyticsLabel)) {
					allLabelPojo.setAnalyticsCategoryName(allCatAnalyticsLabel);
				}
				allLabelPojo.setCategoryName(allCategoryName.toLowerCase());
				List<String> allLabel = new ArrayList<>();
				allLabel.add("All");
				allLabelPojo.setCategoryTag(allLabel);
				categoryList.add(allLabelPojo);
			}
			NodeIterator categories = categoryNode.getNodes();
			while (categories.hasNext()) {
				Node category = categories.nextNode();
				VideoCategory catPojo = new VideoCategory();
				String categoryPath = category.getProperty("category") != null
						? category.getProperty("category").getString()
						: "";
				if (!categoryPath.isEmpty()) {
					catPojo = fetchCategoryDetails(categoryPath, resolver);
				}
				categoryList.add(catPojo);
			}

		}

		LOGGER.debug("categoryList size {}", categoryList.size());
		LOGGER.info("End -> getVideoCategoryList");
		return categoryList;
	}

	/**
	 * Method to fetch the Video Category Page Properties
	 * 
	 * @param categoryPath
	 * @param resolver
	 * @return This method returns the category details
	 * @throws RepositoryException
	 */
	private VideoCategory fetchCategoryDetails(String categoryPath, ResourceResolver resolver)
			throws RepositoryException {
		PageManager pageManager = resolver.adaptTo(PageManager.class);
		String contentPath = categoryPath + Constants.SLASH_JCR_CONTENT;
		Resource resource = resolver.getResource(contentPath);
		VideoCategory catPojo = new VideoCategory();
		Page categoryPage = null;
		if (null != pageManager) {
			if (null != resource) {
				Node categoryNode = resource.adaptTo(Node.class);
				catPojo = fetchCategorySeo(categoryNode);
			}
			categoryPage = pageManager.getPage(categoryPath);
		}
		if (null != categoryPage) {
			String categoryTitle = categoryPage.getNavigationTitle() != null ? categoryPage.getNavigationTitle()
					: categoryPage.getTitle();
			catPojo.setCategoryName(categoryPage.getName());
			catPojo.setCategoryTitle(categoryTitle);
			List<String> tags = new ArrayList<>();
			Tag[] categoryTags = categoryPage.getTags();
			if (null != categoryTags && categoryTags.length > 0 && null != categoryTags[0]) {
				Tag categoryTag = categoryTags[0];
				catPojo.setAnalyticsCategoryName(categoryTag.getTitle());
				for (Tag tag : categoryTags) {
					tags.add(tag.getTagID());
				}
				catPojo.setCategoryTag(tags);
			}
		}
		return catPojo;
	}

	/**
	 * Method to fetch the SEO properties of each category of video.
	 * 
	 * @param categoryNode
	 * @return
	 * @throws RepositoryException
	 */
	private VideoCategory fetchCategorySeo(Node categoryNode) throws RepositoryException {
		VideoCategory catPojo = new VideoCategory();
		if (categoryNode != null) {
			String metaKeys = "";
			String seoTitle = TrainingHelper.checkForProperty(categoryNode, "seoTitle");
			if (seoTitle.isEmpty()) {
				seoTitle = TrainingHelper.checkForProperty(categoryNode, "globalSeoTitle");
			}
			catPojo.setSeotitle(seoTitle);
			String seoUrl = TrainingHelper.checkForProperty(categoryNode, "canonicalUrl");
			catPojo.setSeoUrl(seoUrl);
			String metaDesc = TrainingHelper.checkForProperty(categoryNode, "metaDescription");
			catPojo.setMetaDesc(metaDesc);
			boolean checkMetaKey = categoryNode.hasNode("metaKeywords");
			if (checkMetaKey) {
				Node metaNode = categoryNode.getNode("metaKeywords");
				NodeIterator metaNodes = metaNode.getNodes();
				while (metaNodes.hasNext()) {
					Node metaKey = metaNodes.nextNode();
					String keyWord = metaKey.getProperty("keyword").getString();
					metaKeys = metaKeys.concat(keyWord + ",");
				}
			}
			catPojo.setMetaKeywords(metaKeys);
		}
		return catPojo;
	}

	public List<VideoTile> getPLPVideosManual(String[] plpVideos) {
		LOGGER.info("start of getAllTiles method ");
		List<VideoTile> videoList = new LinkedList<>();
		Map<String, Object> map = new HashMap<>();
		map.put(ResourceResolverFactory.SUBSERVICE, service);
		map.put(ResourceResolverFactory.USER, serviceUser);
		try {
			if (resolverFactory != null) {
				resolver = resolverFactory.getServiceResourceResolver(map);
			}
			if (null != plpVideos) {
				videoList.clear();
				for (String videoItem : plpVideos) {
					VideoTile videoDetails = null;
					String videoThumbnailPath = videoItem;
					Resource authorResource = resolver.resolve(videoThumbnailPath);
					videoDetails = prepareVideoTile(authorResource);
					videoList.add(videoDetails);
				}
			}

		} catch (LoginException e) {
			LOGGER.error("Exception caused in get Tiles By Category", e);
		} finally {
			if (null != resolver && resolver.isLive()) {
				resolver.close();
			}
		}
		LOGGER.info("end of getAllTiles method ");
		return videoList;
	}
}