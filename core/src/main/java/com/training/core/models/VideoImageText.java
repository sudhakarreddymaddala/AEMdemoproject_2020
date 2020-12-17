package com.training.core.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.training.core.pojos.VideoTile;
import com.training.core.services.VideoGalleryService;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class VideoImageText {

	@Inject
	@Optional
	private String imageFileReferenceMainImageDesktop;

	private String ooyalaId;
	@Inject
	VideoGalleryService videoGalleryService;
	private static final Logger LOGGER = LoggerFactory.getLogger(VideoImageText.class);

	public void setVideoGalleryService(VideoGalleryService videoGalleryService) {
		this.videoGalleryService = videoGalleryService;
	}

	private VideoTile videoDetails;
	@Self
	Resource resource;

	@PostConstruct
	protected void init() {
		LOGGER.info("VideoImageText init method  ----> Start");
		if (resource != null) {
			ResourceResolver resolver = resource.getResourceResolver();
			if (imageFileReferenceMainImageDesktop != null) {
				LOGGER.debug("imageFileReferenceMainImageDesktop value is {}", imageFileReferenceMainImageDesktop);
				Resource videoResource = resolver.getResource(imageFileReferenceMainImageDesktop);
				if (null != videoResource) {
					// to be reviewed
					videoDetails = videoGalleryService.prepareVideoTile(videoResource);
					ooyalaId = videoDetails.getVideoId();
					LOGGER.debug("VideoImageText init method  ----> Ooyala Id{}", ooyalaId);
				}
			}
		}
		LOGGER.info("VideoImageText init method  ----> End");
	}

	public void setImageFileReferenceMainImageDesktop(String imageFileReferenceMainImageDesktop) {
		this.imageFileReferenceMainImageDesktop = imageFileReferenceMainImageDesktop;
	}

	public VideoTile getVideoDetails() {
		return videoDetails;
	}

	public void setVideoDetails(VideoTile videoDetails) {
		this.videoDetails = videoDetails;
	}

	public String getOoyalaId() {
		return ooyalaId;
	}

	public void setOoyalaId(String ooyalaId) {
		this.ooyalaId = ooyalaId;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

}
