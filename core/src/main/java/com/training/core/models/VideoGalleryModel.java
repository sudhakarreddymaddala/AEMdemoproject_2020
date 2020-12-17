package com.training.core.models;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
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
import com.training.core.pojos.VideoTile;
import com.training.core.services.VideoGalleryService;
import com.training.core.utils.TrainingSiteConfigurationUtils;
import com.training.core.utils.PropertyReaderUtils;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class VideoGalleryModel {

	private static final Logger LOGGER = LoggerFactory.getLogger(VideoGalleryModel.class);

	private List<VideoTile> videoListManual;
	private List<VideoTile> videoListLanding;
	private List<VideoTile> videoListByCategory;

	@Self
	Resource resource;

	@Self
	Node videoGalleryNode;
	@Inject
	@Optional
	String videoAssetPath;
	@Inject
	private VideoGalleryService videoGalleryService;

	@Inject
	private String[] galleryCategory;

	@PostConstruct
	protected void init() {
		LOGGER.info("GalleryModel init -> start");

		LOGGER.info("GalleryModel init -> End");
	}

	public String getVideoUrl() {
		String videoUrl;

		videoUrl = PropertyReaderUtils.getTrainingPath() + TrainingHelper.getBrandName(resource.getPath())
				+ TrainingHelper.getRelativePath(resource.getPath(), resource) + PropertyReaderUtils.getHomePath()
				+ PropertyReaderUtils.getVideoPath();

		videoUrl = TrainingHelper.checkLink(videoUrl, resource) + Constants.VIDEO_URL;

		return videoUrl;

	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public void setVideoGalleryNode(Node videoGalleryNode) {
		this.videoGalleryNode = videoGalleryNode;
	}

	public void setVideoGalleryService(VideoGalleryService videoGalleryService) {
		this.videoGalleryService = videoGalleryService;
	}

	public void setGalleryCategory(String[] galleryCategory) {
		this.galleryCategory = galleryCategory;
	}

	/**
	 * Method to get Video List in Manual Mode
	 * 
	 * @return videoListManual
	 */
	public List<VideoTile> getVideoListManual() {
		LOGGER.info("GalleryModel getVideoListManual -> start");
		try {
			if (null != videoGalleryNode) {
				String videoGalleryNodePath = videoGalleryNode.getPath();
				videoListManual = videoGalleryService.getVideosForManual(videoGalleryNodePath);

				if (videoListManual.size() > 12) {
					videoListManual = videoListManual.subList(0, 12);
					videoListManual = checkVideoUrl(videoListManual, resource);
					return videoListManual;
				} else {
					videoListManual = checkVideoUrl(videoListManual, resource);
				}
			}
		} catch (RepositoryException e) {
			LOGGER.error("Repository Exception {} ", e);
		}
		LOGGER.info("GalleryModel getVideoListManual -> End");
		return videoListManual;
	}

	/**
	 * Method to fetch the video details from Landing Node
	 * 
	 * @return
	 */
	public List<VideoTile> getVideoListLanding() {
		LOGGER.info("GalleryModel getVideoListLanding -> start");

		try {
			if (null != resource && !resource.getPath().contains("conf")) {
				String videoNodePath = PropertyReaderUtils.getTrainingPath()
						+ TrainingHelper.getBrandName(resource.getPath())
						+ TrainingHelper.getRelativePath(resource.getPath(), resource)
						+ PropertyReaderUtils.getHomePath() + PropertyReaderUtils.getVideoPath()
						+ Constants.JCR_CONTENT_ROOT;
				ResourceResolver resolver = resource.getResourceResolver();
				Resource jcrRootRes = resolver.getResource(videoNodePath);
				if (jcrRootRes != null) {
					Node jcrRootNode = jcrRootRes.adaptTo(Node.class);
					if (jcrRootNode != null) {
						NodeIterator iterator = jcrRootNode.getNodes();
						if (iterator.hasNext()) {
							Node childNode = iterator.nextNode();
							checkAndFetchVideoListLanding(childNode);
						}
					}
				}
				if (videoListLanding.size() > 12) {
					videoListLanding = videoListLanding.subList(0, 12);
					videoListManual = checkVideoUrl(videoListLanding, resource);
					return videoListLanding.subList(0, 12);
				} else {
					videoListManual = checkVideoUrl(videoListLanding, resource);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception Occured {} ", e);
		}
		LOGGER.info("GalleryModel getVideoListLanding -> End");
		return videoListLanding;
	}

	/**
	 * Method to check for Video Detail Node and Fetch Video List from Landing
	 * 
	 * @param childNode
	 * @throws RepositoryException
	 */
	private void checkAndFetchVideoListLanding(Node childNode) throws RepositoryException {
		String videoDetailPath = "";
		if (childNode.getProperty(Constants.SLING_RESOURCETYPE).getString().contains(Constants.VIDEO_DETAIL)) {
			videoDetailPath = childNode.getPath();
			videoListLanding = videoGalleryService.getVideosForManual(videoDetailPath);
		}
	}

	/**
	 * Method to fetch video details by category
	 * 
	 * @return
	 */
	public List<VideoTile> getVideoListByCategory() {
		LOGGER.info("GalleryModel getVideoListByCategory -> start");
		TagManager tagManager = resource.getResourceResolver().adaptTo(TagManager.class);
		if (galleryCategory != null && tagManager != null) {
			Tag galleryTag = tagManager.resolve(galleryCategory[0]);
			String videoGalleryCategoryID = galleryTag.getTagID();
			String pageLocale = "";
			String path;
			path = getVideoSourcePathDetails(pageLocale);
			videoListByCategory = videoGalleryService.getVideosByDate(path, videoGalleryCategoryID, true);
		}

		if (videoListByCategory.size() > 12) {
			videoListByCategory = videoListByCategory.subList(0, 12);
			videoListByCategory = checkVideoUrl(videoListByCategory, resource);
			return videoListByCategory.subList(0, 12);
		} else {
			videoListByCategory = checkVideoUrl(videoListByCategory, resource);
		}
		LOGGER.info("GalleryModel getVideoListByCategory -> end");
		return videoListByCategory;
	}

	/**
	 * @param pageLocale page Locale
	 * @return Method to Fetch Video Details by Date
	 * @return Video List by Date
	 */
	public List<VideoTile> getVideoListByDate() {
		LOGGER.info("GalleryModel getVideoListByDate -> start");
		List<VideoTile> videoListByDate;
		String pageLocale = "";
		String path;
		path = getVideoSourcePathDetails(pageLocale);
		videoListByDate = videoGalleryService.getVideosByDate(path, null, true);
		if (videoListByDate.size() > 12) {
			videoListByDate = videoListByDate.subList(0, 12);
			videoListByDate = checkVideoUrl(videoListByDate, resource);
			return videoListByDate;
		} else {
			videoListByDate = checkVideoUrl(videoListByDate, resource);
		}
		LOGGER.info("GalleryModel getVideoListByDate -> end");
		return videoListByDate;

	}

	/**
	 * Method to return video source path
	 * 
	 * @param pageLocale page Locale
	 * @return path video source path
	 */
	private String getVideoSourcePathDetails(String pageLocale) {
		LOGGER.info("Start of getVideoListByCategoryAndDate method");
		String path;
		if (StringUtils.isNotEmpty(videoAssetPath)) {
			path = videoAssetPath;
		} else {
			if (resource.getPath().contains("/language-masters/")) {
				pageLocale = videoGalleryService.getPageLocaleFromMappings(resource.getPath(), pageLocale);
			} else {
				pageLocale = TrainingHelper.getPageLocale(resource.getPath());
			}
			path = PropertyReaderUtils.getTrainingDamPath() + TrainingHelper.getBrandName(resource.getPath())
					+ PropertyReaderUtils.getVideoDamPath() + pageLocale + PropertyReaderUtils.getVideoPath();
		}
		LOGGER.info("End of getVideoListByCategoryAndDate method");
		return path;
	}

	public List<VideoTile> checkVideoUrl(List<VideoTile> videoList, Resource resource) {
		String currentBrandName = TrainingHelper.getBrandName(resource.getPath());
		if (currentBrandName.equals(TrainingSiteConfigurationUtils.getFisherPriceKidsBrandName())) {
			for (VideoTile videoTile : videoList) {
				String path = videoTile.getVideoThumbnail();
				path = path.substring(0, path.lastIndexOf('/'));
				path = path.replace(PropertyReaderUtils.getTrainingDamPath(), PropertyReaderUtils.getTrainingDamPath());

				path = path.replace(
						PropertyReaderUtils.getVideoDamPath()
								+ TrainingHelper.fetchLocaleFromDam(videoTile.getVideoThumbnail(), resource),
						TrainingHelper.getRelativePath(resource.getPath(), resource));
				videoTile.setVideoUrl(TrainingHelper.checkLink(path, resource) + Constants.VIDEO_URL);
			}

		}
		return videoList;
	}

	public void setVideoListManual(List<VideoTile> videoListManual) {
		this.videoListManual = videoListManual;
	}

}
