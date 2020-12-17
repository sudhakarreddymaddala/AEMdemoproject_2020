package com.training.core.pojos;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
import java.util.List;

public class VideoCategory {
	private String categoryTitle;
	private List<String> categoryTag;
	private String categoryName;
	private String seotitle;
	private String seoUrl;
	private String metaKeywords;
	private String metaDesc;
	private boolean selected = false;
	private String analyticsCategoryName;

	public String getCategoryTitle() {
		return categoryTitle;
	}

	public void setCategoryTitle(String categoryTitle) {
		this.categoryTitle = categoryTitle;
	}

	public List<String> getCategoryTag() {
		return categoryTag;
	}

	public void setCategoryTag(List<String> categoryTag) {
		this.categoryTag = categoryTag;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
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

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String toString() {
		return "VideoCategory [categoryTitle=" + categoryTitle + ", categoryTag=" + categoryTag + ", categoryName="
				+ categoryName + ", seotitle=" + seotitle + ", seoUrl=" + seoUrl + ", metaKeywords=" + metaKeywords
				+ ", metaDesc=" + metaDesc + ", selected=" + selected + ", analyticsCategoryName="
				+ analyticsCategoryName + "]";
	}

	public String getAnalyticsCategoryName() {
		return analyticsCategoryName;
	}

	public void setAnalyticsCategoryName(String analyticsCategoryName) {
		this.analyticsCategoryName = analyticsCategoryName;
	}
}
