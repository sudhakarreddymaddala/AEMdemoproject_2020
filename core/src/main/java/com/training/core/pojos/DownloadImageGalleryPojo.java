package com.training.core.pojos;

import java.util.List;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
public class DownloadImageGalleryPojo {

	private String thumbnailImage;

	private String thumbnailTitle;

	private String thumbnailDescription;

	private String altTextThumbnail;

	private String downloadCtaLabel;

	private String downloadCtaLink;

	private String openCtaLinksIn;
	
	private String alwaysEnglish;
	
	private String disabledDownloadFile;
	
	public String getDisabledDownloadFile() {
		return disabledDownloadFile;
	}

	public void setDisabledDownloadFile(String disabledDownloadFile) {
		this.disabledDownloadFile = disabledDownloadFile;
	}

	private List<InterstitialPojo> interstitialDetailsList;


	public List<InterstitialPojo> getInterstitialDetailsList() {
		return interstitialDetailsList;
	}

	public void setInterstitialDetailsList(List<InterstitialPojo> interstitialDetailsList) {
		this.interstitialDetailsList = interstitialDetailsList;
	}

	public String getThumbnailImage() {
		return thumbnailImage;
	}

	public void setThumbnailImage(String thumbnailImage) {
		this.thumbnailImage = thumbnailImage;
	}

	public String getThumbnailTitle() {
		return thumbnailTitle;
	}

	public void setThumbnailTitle(String thumbnailTitle) {
		this.thumbnailTitle = thumbnailTitle;
	}

	public String getThumbnailDescription() {
		return thumbnailDescription;
	}

	public void setThumbnailDescription(String thumbnailDescription) {
		this.thumbnailDescription = thumbnailDescription;
	}

	public String getAltTextThumbnail() {
		return altTextThumbnail;
	}

	public void setAltTextThumbnail(String altTextThumbnail) {
		this.altTextThumbnail = altTextThumbnail;
	}

	public String getOpenCtaLinksIn() {
		return openCtaLinksIn;
	}

	public void setOpenCtaLinksIn(String openCtaLinksIn) {
		this.openCtaLinksIn = openCtaLinksIn;
	}

	public String getDownloadCtaLabel() {
		return downloadCtaLabel;
	}

	public void setDownloadCtaLabel(String ctaLabel) {
		this.downloadCtaLabel = ctaLabel;
	}

	public String getDownloadCtaLink() {
		return downloadCtaLink;
	}

	public void setDownloadCtaLink(String ctaLink) {
		this.downloadCtaLink = ctaLink;
	}
	
	public String getAlwaysEnglish() {
		return alwaysEnglish;
	}

	public void setAlwaysEnglish(String alwaysEnglish) {
		this.alwaysEnglish = alwaysEnglish;
	}


}
