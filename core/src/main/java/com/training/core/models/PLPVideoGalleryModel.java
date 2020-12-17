package com.training.core.models;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.training.core.pojos.VideoTile;
import com.training.core.services.VideoGalleryService;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PLPVideoGalleryModel {

	private static final Logger LOGGER = LoggerFactory.getLogger(VideoGalleryModel.class);

	private List<VideoTile> videoManualList = new LinkedList<>();
	private List<VideoTile> videoByDateList = new LinkedList<>();
	private List<VideoTile> videoByCategoryList = new LinkedList<>();

	@Self
	Resource resource;

	@Self
	Node videoGalleryNode;
	@Inject
	@Optional
	String plpVideoAssetPath;
	@Inject
	private VideoGalleryService videoGalleryService;
	@Inject
	@Optional
	private String[] plpVideos;

	@Inject
	@Optional
	private String[] plpGalleryCategory;

	@PostConstruct
	protected void init() {
		LOGGER.info("PLPVideoGalleryModel init -> start");

		LOGGER.info("PLPVideoGalleryModel init -> End");
	}

	/**
	 * Method to fetch the PLP Videos when authored Manually
	 * 
	 * @return
	 */
	public List<VideoTile> getVideoManualList() {
		LOGGER.info("getVideoManualList -> Start");
		videoManualList.clear();
		if (plpVideos != null) {
			videoManualList = videoGalleryService.getPLPVideosManual(plpVideos);
		}
		if (videoManualList.size() > 12) {
			return videoManualList.subList(0, 12);
		}
		return videoManualList;
	}

	public void setVideoManualList(List<VideoTile> videoManualList) {
		this.videoManualList = videoManualList;
	}

	public List<VideoTile> getVideoByDateList() {
		LOGGER.info("PLPGalleryModel getVideoByDateList -> start");
		videoByDateList = videoGalleryService.getVideosByDate(plpVideoAssetPath, null, true);
		if (videoByDateList.size() > 12) {
			videoByDateList = videoByDateList.subList(0, 12);
			return videoByDateList;
		}
		LOGGER.info("PLPGalleryModel getVideoByDateList -> end");

		return videoByDateList;
	}

	public void setVideoByDateList(List<VideoTile> videoByDateList) {
		this.videoByDateList = videoByDateList;
	}

	public List<VideoTile> getVideoByCategoryList() {
		LOGGER.info("PLPGalleryModel getVideoByCategoryList -> start");
		TagManager tagManager = resource.getResourceResolver().adaptTo(TagManager.class);
		if (plpGalleryCategory != null && tagManager != null && plpVideoAssetPath != null) {
			Tag galleryTag = tagManager.resolve(plpGalleryCategory[0]);
			String videoGalleryCategoryID = galleryTag.getTagID();
			videoByCategoryList = videoGalleryService.getVideosByDate(plpVideoAssetPath, videoGalleryCategoryID, true);
		}

		if (videoByCategoryList.size() > 12) {
			videoByCategoryList = videoByCategoryList.subList(0, 12);
			return videoByCategoryList.subList(0, 12);
		}
		LOGGER.info("PLPGalleryModel getVideoByCategoryList -> end");
		return videoByCategoryList;
	}

	public void setVideoByCategoryList(List<VideoTile> videoByCategoryList) {
		this.videoByCategoryList = videoByCategoryList;
	}

	public String[] getPlpGalleryCategory() {
		return plpGalleryCategory;
	}

	public void setPlpGalleryCategory(String[] plpGalleryCategory) {
		this.plpGalleryCategory = plpGalleryCategory;
	}
}