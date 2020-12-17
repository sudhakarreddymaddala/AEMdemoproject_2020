package com.training.core.pojos;

public class ProductAsset {

	private String productImage;
	private String ProductAltText;
	private boolean isVideo;
	private String ooyalaId;
	
	public String getProductImage() {
		return productImage;
	}
	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}
	public String getProductAltText() {
		return ProductAltText;
	}
	public void setProductAltText(String productAltText) {
		ProductAltText = productAltText;
	}
	public boolean isVideo() {
		return isVideo;
	}
	public void setVideo(boolean isVideo) {
		this.isVideo = isVideo;
	}
	public String getOoyalaId() {
		return ooyalaId;
	}
	public void setOoyalaId(String ooyalaId) {
		this.ooyalaId = ooyalaId;
	}
	
}