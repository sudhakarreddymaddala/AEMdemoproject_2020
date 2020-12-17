package com.training.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.training.core.helper.TrainingHelper;
import com.training.core.pojos.PromoCarouselPojo;
import com.training.core.pojos.VideoTile;
import com.training.core.services.MultifieldReader;
import com.training.core.services.VideoGalleryService;

/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PromoCarouselModel {

	private static final Logger LOGGER = LoggerFactory.getLogger(PromoCarouselModel.class);
	@SlingObject
	private SlingHttpServletRequest request;
	@Inject
	@Via("resource")
	@Optional
	private MultifieldReader multifieldReader;
	
	@Inject
	Resource resource;

	public void setMultifieldReader(MultifieldReader multifieldReader) {
		this.multifieldReader = multifieldReader;
	}

	public void setPromcarousel(Node promcarousel) {
		this.promcarousel = promcarousel;
	}

	@Inject
	@Via("resource")
	@Optional
	private Node promcarousel;
	@Inject
	VideoGalleryService videoGalleryService;

	public void setVideoGalleryService(VideoGalleryService videoGalleryService) {
		this.videoGalleryService = videoGalleryService;
	}

	@Inject
	@Via("resource")
	@Optional
	private int transitionTime;

	List<PromoCarouselPojo> promoCarouselList;

	/**
	 * The init method to fetch the List of Promo Carousel Contents
	 */
	@PostConstruct
	protected void init() {
		LOGGER.info("PromoCarouselModel init method  ----> Start");
		if (promcarousel != null && null!=resource) {

			promoCarouselList = new ArrayList<>();
			Map<String, ValueMap> multifieldProperty;
			multifieldProperty = multifieldReader.propertyReader(promcarousel);
			for (Map.Entry<String, ValueMap> entry : multifieldProperty.entrySet()) {
				String carouselImage = entry.getValue().get("crlImage", String.class);
				PromoCarouselPojo carouselLinks = new PromoCarouselPojo();
				if (carouselImage.contains("videos")) {
					Resource imgResource = request.getResourceResolver().getResource(carouselImage);
					if (imgResource != null) {
						VideoTile videoDetail = videoGalleryService.prepareVideoTile(imgResource);
						carouselLinks.setOoyalaId(videoDetail.getVideoId());
					}
				} else {
					carouselLinks.setCarouselImage(entry.getValue().get("crlImage", String.class));
				}

				carouselLinks.setAlignment(entry.getValue().get("alignment", String.class));

				carouselLinks.setAwalysEnglish(entry.getValue().get("awalysEnglish", String.class));
				carouselLinks.setAdobeTrackingForCta(entry.getValue().get("adobeTrackingForCta", String.class));

				carouselLinks.setBackgroundColor(entry.getValue().get("backgroundColor", String.class));

				carouselLinks.setBlrImage(entry.getValue().get("blrImage", String.class));

				carouselLinks.setBackgroundoption(entry.getValue().get("backgroundoption", String.class));

				carouselLinks.setCtaLabel(entry.getValue().get("ctaLabel", String.class));
				carouselLinks.setCtaLink(TrainingHelper.checkLink(entry.getValue().get("ctaLink", String.class),resource));
				carouselLinks.setDescription(entry.getValue().get("description", String.class));
				carouselLinks.setTitle(entry.getValue().get("title", String.class));
				carouselLinks.setImageAltText(entry.getValue().get("imageAltText", String.class));
				carouselLinks.setImageUrl(entry.getValue().get("mobileImage", String.class));
				carouselLinks.setOpenCtalinksin(entry.getValue().get("clrTargetUrl", String.class));
				promoCarouselList.add(carouselLinks);

			}

		}
		LOGGER.info("PromoCarouselModel init method  ----> End");
	}

	public List<PromoCarouselPojo> getPromoCarouselList() {
		return promoCarouselList;
	}

	public int getTransitionTime() {
		return transitionTime * 1000;
	}

	public void setSlingHttpServletRequest(SlingHttpServletRequest request) {
		this.request = request;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
}
