package com.training.core.pojos;

import java.util.Date;

/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
public class CharacterPojo {

	private String charTitle;

	private String charImage;

	private String charPath;

	private String charTagId;

	private String charSubtitle;

	private String charDesc;

	private String charExtraDesc;

	private String charCategory; 

	private String charImgAltText;

	private String charSecondayImage;

	private String charSecondayImageAltText;

	private String charThumbImage;

	private String charThumbImageAltText;

	private String charPageName;
	
	private Date lastModified;

	@Override
	public String toString() {
		return "CharacterPojo [charTitle=" + charTitle + ", charImage=" + charImage + ", charPath=" + charPath
				+ ", charTagId=" + charTagId + ", charSubtitle=" + charSubtitle + ", charDesc=" + charDesc
				+ ", charExtraDesc=" + charExtraDesc + ", charCategory=" + charCategory + ", charImgAltText="
				+ charImgAltText + ", charSecondayImage=" + charSecondayImage + ", charSecondayImageAltText="
				+ charSecondayImageAltText + ", charThumbImage=" + charThumbImage + ", charThumbImageAltText="
				+ charThumbImageAltText + ", getCharSubtitle()=" + getCharSubtitle() + ", getCharDesc()="
				+ getCharDesc() + ", getCharExtraDesc()=" + getCharExtraDesc() + ", getCharCategory()="
				+ getCharCategory() + ", getCharImgAltText()=" + getCharImgAltText() + ", getCharSecondayImage()="
				+ getCharSecondayImage() + ", getCharSecondayImageAltText()=" + getCharSecondayImageAltText()
				+ ", getCharThumbImage()=" + getCharThumbImage() + ", getCharThumbImageAltText()="
				+ getCharThumbImageAltText() + ", getCharPath()=" + getCharPath() + ", getCharTitle()=" + getCharTitle()
				+ ", getCharImage()=" + getCharImage() + ", getCharTagId()=" + getCharTagId() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

	public String getCharSubtitle() {
		return charSubtitle;
	}

	public void setCharSubtitle(String charSubtitle) {
		this.charSubtitle = charSubtitle;
	}

	public String getCharDesc() {
		return charDesc;
	}

	public void setCharDesc(String charDesc) {
		this.charDesc = charDesc;
	}

	public String getCharExtraDesc() {
		return charExtraDesc;
	}

	public void setCharExtraDesc(String charExtraDesc) {
		this.charExtraDesc = charExtraDesc;
	}

	public String getCharCategory() {
		return charCategory;
	}

	public void setCharCategory(String charCategory) {
		this.charCategory = charCategory;
	}

	public String getCharImgAltText() {
		return charImgAltText;
	}

	public void setCharImgAltText(String charImgAltText) {
		this.charImgAltText = charImgAltText;
	}

	public String getCharSecondayImage() {
		return charSecondayImage;
	}

	public void setCharSecondayImage(String charSecondayImage) {
		this.charSecondayImage = charSecondayImage;
	}

	public String getCharSecondayImageAltText() {
		return charSecondayImageAltText;
	}

	public void setCharSecondayImageAltText(String charSecondayImageAltText) {
		this.charSecondayImageAltText = charSecondayImageAltText;
	}

	public String getCharThumbImage() {
		return charThumbImage;
	}

	public void setCharThumbImage(String charThumbImage) {
		this.charThumbImage = charThumbImage;
	}

	public String getCharThumbImageAltText() {
		return charThumbImageAltText;
	}

	public void setCharThumbImageAltText(String charThumbImageAltText) {
		this.charThumbImageAltText = charThumbImageAltText;
	}

	public String getCharPath() {
		return charPath;
	}

	public void setCharPath(String charPath) {
		this.charPath = charPath;
	}

	public String getCharTitle() {
		return charTitle;
	}

	public void setCharTitle(String charTitle) {
		this.charTitle = charTitle;
	}

	public String getCharImage() {
		return charImage;
	}

	public void setCharImage(String charImage) {
		this.charImage = charImage;
	}

	public String getCharTagId() {
		return charTagId;
	}

	public void setCharTagId(String charTagId) {
		this.charTagId = charTagId;
	}

	public String getCharPageName() {
		return charPageName;
	}

	public void setCharPageName(String charPageName) {
		this.charPageName = charPageName;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

}
