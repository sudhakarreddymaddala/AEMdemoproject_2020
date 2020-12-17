package com.training.core.pojos;

import java.util.List;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
/**
 * A simple pojo for page properties.
 */
public class SiteNavigationPojo {

	private String pageName;
	private String name;
	private String pageUrl;
	private String thumbnailImg;
	private Boolean isRedirect = false;
	private String linkingName;
	private boolean pageActive = false;
	private String urlTarget;
	private String adobeTrackingNameForPage;
	private String thumbnailAltText;
	private boolean isNotLinkable;
	private String specialLink;
	private List<SiteNavigationPojo> childPageList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public List<SiteNavigationPojo> getChildPageList() {
		return childPageList;
	}

	public void setChildPageList(List<SiteNavigationPojo> childPageList) {
		this.childPageList = childPageList;
	}

	public Boolean getIsRedirect() {
		return isRedirect;
	}

	public void setIsRedirect(Boolean isRedirect) {
		this.isRedirect = isRedirect;
	}

	public String getThumbnailImg() {
		return thumbnailImg;
	}

	public void setThumbnailImg(String thumbnailImg) {
		this.thumbnailImg = thumbnailImg;
	}

	public boolean isPageActive() {
		return pageActive;
	}

	public void setPageActive(boolean setActive) {
		this.pageActive = setActive;
	}

	public String getUrlTarget() {
		return urlTarget;
	}

	public void setUrlTarget(String urlTarget) {
		this.urlTarget = urlTarget;
	}

	public String getLinkingName() {
		return linkingName;
	}

	public void setLinkingName(String linkingName) {
		this.linkingName = linkingName;
	}

	public String getAdobeTrackingNameForPage() {
		return adobeTrackingNameForPage;
	}

	public void setAdobeTrackingNameForPage(String adobeTrackingNameForPage) {
		this.adobeTrackingNameForPage = adobeTrackingNameForPage;
	}

	public String getThumbnailAltText() {
		return thumbnailAltText;
	}

	public void setThumbnailAltText(String thumbnailAltText) {
		this.thumbnailAltText = thumbnailAltText;
	}

	public boolean isNotLinkable() {
		return isNotLinkable;
	}

	public void setNotLinkable(boolean isNotLinkable) {
		this.isNotLinkable = isNotLinkable;
	}

	public String getSpecialLink() {
		return specialLink;
	}

	public void setSpecialLink(String specialLink) {
		this.specialLink = specialLink;
	}

}
