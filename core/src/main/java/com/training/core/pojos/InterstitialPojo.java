package com.training.core.pojos;

public class InterstitialPojo {
	private String interstitialLogoSrc;
	private String interstitialLogoAlt;
	private String interstitialUrl;
	private String interstitialTarget;

	public String getInterstitialLogoSrc() {
		return interstitialLogoSrc;
	}

	public void setInterstitialLogoSrc(String interstitialLogoSrc) {
		this.interstitialLogoSrc = interstitialLogoSrc;
	}

	public String getInterstitialLogoAlt() {
		return interstitialLogoAlt;
	}

	public void setInterstitialLogoAlt(String interstitialLogoAlt) {
		this.interstitialLogoAlt = interstitialLogoAlt;
	}

	public String getInterstitialUrl() {
		return interstitialUrl;
	}

	public void setInterstitialUrl(String interstitialUrl) {
		this.interstitialUrl = interstitialUrl;
	}

	public String getInterstitialTarget() {
		return interstitialTarget;
	}

	public void setInterstitialTarget(String interstitialTarget) {
		this.interstitialTarget = interstitialTarget;
	}
	
	@Override
	public String toString() {
		return "InterstitialPojo [interstitialLogoSrc=" + interstitialLogoSrc + ", interstitialLogoAlt=" + interstitialLogoAlt
				+ ", interstitialUrl=" + interstitialUrl + "]";
	}
	

}
