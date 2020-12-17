package com.training.core.pojos;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
public class FooterPojo {
	private String linkText;
	private String linkURL;
	private String linkTarget;
	private String alwaysEnglish;
	
	public String getLinkTarget() {
		return linkTarget;
	}

	public void setLinkTarget(String linkTarget) {
		this.linkTarget = linkTarget;
	}

	public String getLinkText() {
		return linkText;
	}

	public void setLinkText(String linkText) {
		this.linkText = linkText;
	}

	public String getLinkURL() {
		return linkURL;
	}

	public void setLinkURL(String linkURL) {
		this.linkURL = linkURL;
	}

	@Override
	public String toString() {
		return "FooterPojo [linkText=" + linkText + ", linkURL=" + linkURL + ", linkTarget=" + linkTarget + "]";
	}

	public String getAlwaysEnglish() {
		return alwaysEnglish;
	}

	public void setAlwaysEnglish(String alwaysEnglish) {
		this.alwaysEnglish = alwaysEnglish;
	}

	
}
