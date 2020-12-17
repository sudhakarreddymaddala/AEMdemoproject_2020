package com.training.core.pojos;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
public class RetailerPojo {
	private String retailerLogoSrc;
	private String retailLogoAlt;
	private String retailerUrl;
	private String retailerTarget;

	public String getRetailerLogoSrc() {
		return retailerLogoSrc;
	}

	public void setRetailerLogoSrc(String retailerLogoSrc) {
		this.retailerLogoSrc = retailerLogoSrc;
	}

	public String getRetailLogoAlt() {
		return retailLogoAlt;
	}

	public void setRetailLogoAlt(String retailLogoAlt) {
		this.retailLogoAlt = retailLogoAlt;
	}

	public String getRetailerUrl() {
		return retailerUrl;
	}

	public void setRetailerUrl(String retailerUrl) {
		this.retailerUrl = retailerUrl;
	}

	public String getRetailerTarget() {
		return retailerTarget;
	}

	public void setRetailerTarget(String retailerTarget) {
		this.retailerTarget = retailerTarget;
	}

	@Override
	public String toString() {
		return "RetailerPojo [retailerLogoSrc=" + retailerLogoSrc + ", retailLogoAlt=" + retailLogoAlt
				+ ", retailerUrl=" + retailerUrl + "]";
	}

}
