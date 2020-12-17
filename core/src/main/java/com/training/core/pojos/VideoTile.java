package com.training.core.pojos;

import java.util.List;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
public class VideoTile {
	private String videoThumbnail;
	private String thumbnailAltTxt;
	private String videoTitle;
	private String videoId;
	private String videoDesc;
	private String videoUrl;
	private List<String> videoTags;
	private String videoCategory;
	private String seotitle;
	private String seoUrl;
	private String metaKeywords;
	private String metaDesc;
	private String titleAlign;
	private String colLayout;
	private String videoName;
	private boolean selected = false;
	private String alwaysEnglish;

	public String getVideoThumbnail() {
		return videoThumbnail;
	}

	public void setVideoThumbnail(String videoThumbnail) {
		this.videoThumbnail = videoThumbnail;
	}

	public String getThumbnailAltTxt() {
		return thumbnailAltTxt;
	}

	public void setThumbnailAltTxt(String thumbnailAltTxt) {
		this.thumbnailAltTxt = thumbnailAltTxt;
	}

	public String getVideoTitle() {
		return videoTitle;
	}

	public void setVideoTitle(String videoTitle) {
		this.videoTitle = videoTitle;
	}

	public String getVideoDesc() {
		return videoDesc;
	}

	public void setVideoDesc(String videoDesc) {
		this.videoDesc = videoDesc;
	}

	public List<String> getVideoTags() {
		return videoTags;
	}

	public void setVideoTags(List<String> videoTags) {
		this.videoTags = videoTags;
	}

	public String getSeotitle() {
		return seotitle;
	}

	public void setSeotitle(String seotitle) {
		this.seotitle = seotitle;
	}

	public String getSeoUrl() {
		return seoUrl;
	}

	public void setSeoUrl(String seoUrl) {
		this.seoUrl = seoUrl;
	}

	public String getMetaKeywords() {
		return metaKeywords;
	}

	public void setMetaKeywords(String metaKeywords) {
		this.metaKeywords = metaKeywords;
	}

	public String getMetaDesc() {
		return metaDesc;
	}

	public void setMetaDesc(String metaDesc) {
		this.metaDesc = metaDesc;
	}

	public String getVideoCategory() {
		return videoCategory;
	}

	public void setVideoCategory(String videoCategory) {
		this.videoCategory = videoCategory;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getTitleAlign() {
		return titleAlign;
	}

	public void setTitleAlign(String titleAlign) {
		this.titleAlign = titleAlign;
	}

	public String getColLayout() {
		return colLayout;
	}

	public void setColLayout(String colLayout) {
		this.colLayout = colLayout;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getVideoName() {
		return videoName;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	public String getAlwaysEnglish() {
		return alwaysEnglish;
	}

	public void setAlwaysEnglish(String alwaysEnglish) {
		this.alwaysEnglish = alwaysEnglish;
	}

	@Override
	public String toString() {
		return "VideoTile videoThumbnail=" + videoThumbnail + ", thumbnailAltTxt=" + thumbnailAltTxt + ", videoTitle="
				+ videoTitle + ", videoId=" + videoId + ", videoDesc=" + videoDesc + ", videoTags=" + videoTags
				+ ", videoCategory=" + videoCategory + ", seotitle=" + seotitle + ", seoUrl=" + seoUrl
				+ ", metaKeywords=" + metaKeywords + ", metaDesc=" + metaDesc + ", titleAlign=" + titleAlign
				+ ", colLayout=" + colLayout + ", videoName=" + videoName + ", selected=" + selected
				+ ", alwaysEnglish=" + alwaysEnglish;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

}