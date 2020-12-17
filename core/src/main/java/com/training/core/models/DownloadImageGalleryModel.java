package com.training.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.training.core.pojos.DownloadImageGalleryPojo;
import com.training.core.pojos.InterstitialPojo;
import com.training.core.services.MultifieldReader;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DownloadImageGalleryModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadImageGalleryModel.class);

	@Inject
	@Optional
	private MultifieldReader multifieldReader;

	@Inject
	@Optional
	private Node downloadImage;

	public void setMultifieldReader(MultifieldReader multifieldReader) {
		this.multifieldReader = multifieldReader;
	}

	public void setDownloadImage(Node downloadImage) {
		this.downloadImage = downloadImage;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public void setDownloadImageGalleryList(List<DownloadImageGalleryPojo> downloadImageGalleryList) {
		this.downloadImageGalleryList = downloadImageGalleryList;
	}

	public void setInterstitialDetailsList(List<InterstitialPojo> interstitialDetailsList) {
		this.interstitialDetailsList = interstitialDetailsList;
	}

	@Self
	Resource resource;

	List<DownloadImageGalleryPojo> downloadImageGalleryList;

	List<InterstitialPojo> interstitialDetailsList = new ArrayList<>();

	/**
	 * The init method to fetch the list of Download the Gallery Details.
	 */
	@PostConstruct
	protected void init() {
		LOGGER.info("init method of DownloadImageGalleryModel start");
		if (downloadImage != null && null != resource) {
			downloadImageGalleryList = new ArrayList<>();
			Map<String, ValueMap> multifieldProperty;
			multifieldProperty = multifieldReader.propertyReader(downloadImage);
			for (Map.Entry<String, ValueMap> entry : multifieldProperty.entrySet()) {
				DownloadImageGalleryPojo downloadfieldList = new DownloadImageGalleryPojo();
				downloadfieldList.setThumbnailImage(entry.getValue().get("thumbnailImage", String.class));
				downloadfieldList.setDisabledDownloadFile(entry.getValue().get("disabledDownloadFile", String.class));
				downloadfieldList.setThumbnailTitle(entry.getValue().get("thumbnailTitle", String.class));
				downloadfieldList.setThumbnailDescription(entry.getValue().get("thumbnailDescription", String.class));
				downloadfieldList.setAltTextThumbnail(entry.getValue().get("altTexTthumbnail", String.class));
				downloadfieldList.setDownloadCtaLabel(entry.getValue().get("downloadCtaLabel", String.class));
				downloadfieldList.setDownloadCtaLink(entry.getValue().get("downloadCtaLink", String.class));
				downloadfieldList.setOpenCtaLinksIn(entry.getValue().get("openCtaLinksIn", String.class));
				downloadfieldList.setAlwaysEnglish(entry.getValue().get("alwaysEnglish", String.class));
				String openCtaLink = entry.getValue().get("openCtaLinksIn", String.class);
				LOGGER.debug("openCtaLink value of DownloadImageGalleryModel is {}",openCtaLink);
				if (openCtaLink != null && openCtaLink.equalsIgnoreCase("interstitial")) {
					try {
						checkInterstitialDetails(downloadfieldList, entry);
					} catch (Exception e) {
						LOGGER.error(
								"Exception occured in reteriving interstial apps in download gallery component {} ", e);
					}

				}
				downloadImageGalleryList.add(downloadfieldList);
				LOGGER.debug("downloadImageGalleryList size of DownloadImageGalleryModel is {}",downloadImageGalleryList.size());
			}

		}
		LOGGER.info("init method of DownloadImageGalleryModel end");
	}

	public List<DownloadImageGalleryPojo> getDownloadImageGalleryList() {
		return downloadImageGalleryList;
	}

	/**
	 * method to check the interstitial details
	 * @param downloadfieldList
	 * @param entry
	 * @throws RepositoryException
	 */
	private void checkInterstitialDetails(DownloadImageGalleryPojo downloadfieldList, Map.Entry<String, ValueMap> entry)
			throws RepositoryException {
		LOGGER.info("checkInterstitialDetails method of DownloadImageGalleryModel start");
		if (downloadImage.hasNodes()) {
			ResourceResolver res = resource.getResourceResolver();
			Resource itemRes = res.getResource(downloadImage.getPath() + "/" + entry.getKey());
			if (itemRes != null) {
				Node downloadList = itemRes.adaptTo(Node.class);
				if (downloadList != null && downloadList.hasNodes()) {
					Resource interstitialResource = res.getResource(downloadList.getPath() + "/interstitialDetailList");
					if (interstitialResource != null) {
						getInterstitialDetails(interstitialResource, downloadfieldList);
					}
				}
			}
		}
		LOGGER.info("checkInterstitialDetails method of DownloadImageGalleryModel end");
	}

	/**
	 * method to fetch the interstitial details
	 * @param interstitialResource
	 * @param downloadfieldList
	 */
	private void getInterstitialDetails(Resource interstitialResource, DownloadImageGalleryPojo downloadfieldList) {
		LOGGER.info("getInterstitialDetails method of DownloadImageGalleryModel start");
		Node interstitialList = interstitialResource.adaptTo(Node.class);
		if (interstitialList != null) {
			Map<String, ValueMap> interstitialMultifieldProperty;
			interstitialMultifieldProperty = multifieldReader.propertyReader(interstitialList);
			for (Map.Entry<String, ValueMap> interstitialEntry : interstitialMultifieldProperty.entrySet()) {
				InterstitialPojo interstitialDetail = new InterstitialPojo();
				interstitialDetail
						.setInterstitialLogoSrc(interstitialEntry.getValue().get("interstitialLogo", String.class));
				interstitialDetail
						.setInterstitialLogoAlt(interstitialEntry.getValue().get("interstitialLogoAlt", String.class));
				interstitialDetail
						.setInterstitialUrl(interstitialEntry.getValue().get("interstitialUrl", String.class));
				interstitialDetail
						.setInterstitialTarget(interstitialEntry.getValue().get("interstitialTarget", String.class));
				interstitialDetailsList.add(interstitialDetail);

			}
			downloadfieldList.setInterstitialDetailsList(interstitialDetailsList);
		}
		LOGGER.info("getInterstitialDetails method of DownloadImageGalleryModel end");
	}

}
