package com.training.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.training.core.pojos.VideoTile;
import com.training.core.services.VideoGalleryService;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TrainingVideo {
	@Inject
	@Optional
	private String videoThumbnail;
	@Self
	Resource resource;

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public void setVideoGalleryService(VideoGalleryService videoGalleryService) {
		this.videoGalleryService = videoGalleryService;
	}

	@Inject
	private VideoGalleryService videoGalleryService;
	private static final Logger LOGGER = LoggerFactory.getLogger(TrainingVideo.class);
	private VideoTile videoDetails;

	/**
	 * The init Method to Fetch Details for Video Thumbnail
	 */
	@PostConstruct
	protected void init() {
		LOGGER.info("PlayVideo init method  ----> Start");
		if (resource != null) {
			ResourceResolver resolver = resource.getResourceResolver();
			if (videoThumbnail != null) {
				Resource videoResource = resolver.getResource(videoThumbnail);
				if (null != videoResource) {
					videoDetails = videoGalleryService.prepareVideoTile(videoResource);
				}
				videoDetails.setVideoThumbnail(videoThumbnail);
			}
		}
		LOGGER.info("PlayVideo init method  ----> End");
	}

	public void setVideoThumbnail(String videoThumbnail) {
		this.videoThumbnail = videoThumbnail;
	}

	public VideoTile getVideoDetails() {
		return videoDetails;
	}

	public void setVideoDetails(VideoTile videoDetails) {
		this.videoDetails = videoDetails;
	}
}
