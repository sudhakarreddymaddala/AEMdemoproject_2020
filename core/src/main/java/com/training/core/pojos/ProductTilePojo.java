package com.training.core.pojos;

import java.util.List;

public class ProductTilePojo {

	private String productPageName;
	private String productPagePath;
	private String productThumbnail;
	private String thumbnailAltTxt;
	private String productCategory;
	private String productTitle;
	private String productThumbnailHover;
	private String thumbnailHoverAltTxt;
	private List<String> productTags;
	private boolean selected;
	private List<ProductAsset> productImages;
	private String productId;
	private String age;
	private String description;
	private String alwaysEnglish;
	
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	public String getAge() {
		return age;
	}

	public void setage(String age) {
		this.age = age;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public List<ProductAsset> getProductImages() {
		return productImages;
	}

	public void setProductImages(List<ProductAsset> productImages) {
		this.productImages = productImages;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public String getProductPageName() {
		return productPageName;
	}

	public void setProductPageName(String productPageName) {
		this.productPageName = productPageName;
	}

	public String getProductPagePath() {
		return productPagePath;
	}

	public void setProductPagePath(String productPagePath) {
		this.productPagePath = productPagePath;
	}

	public String getProductThumbnail() {
		return productThumbnail;
	}

	public void setProductThumbnail(String productThumbnail) {
		this.productThumbnail = productThumbnail;
	}

	public String getThumbnailAltTxt() {
		return thumbnailAltTxt;
	}

	public void setThumbnailAltTxt(String thumbnailAltTxt) {
		this.thumbnailAltTxt = thumbnailAltTxt;
	}

	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}

	public List<String> getProductTags() {
		return productTags;
	}

	public void setProductTags(List<String> productTags) {
		this.productTags = productTags;
	}
	
	public String getProductThumbnailHover() {
		return productThumbnailHover;
	}

	public void setProductThumbnailHover(String productThumbnailHover) {
		this.productThumbnailHover = productThumbnailHover;
	}

	public String getThumbnailHoverAltTxt() {
		return thumbnailHoverAltTxt;
	}

	public void setThumbnailHoverAltTxt(String thumbnailHoverAltTxt) {
		this.thumbnailHoverAltTxt = thumbnailHoverAltTxt;
	}
	
	public String getAlwaysEnglish() {
		return alwaysEnglish;
	}

	public void setAlwaysEnglish(String alwaysEnglish) {
		this.alwaysEnglish = alwaysEnglish;
	}

	@Override
	public String toString() {
		return "TilePojo productPageName=" + productPageName + ", productPagePath=" + productPagePath + ", productThumbnail=" + productThumbnail
				+ ", thumbnailAltTxt=" + thumbnailAltTxt + ", productCategory=" + productCategory + ", productTitle=" + productTitle
				+ ", productThumbnailHover=" + productThumbnailHover + ", thumbnailHoverAltTxt=" + thumbnailHoverAltTxt + ", productTags=" + productTags;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
