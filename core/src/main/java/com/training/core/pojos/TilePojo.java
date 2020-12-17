package com.training.core.pojos;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
import java.util.List;

public class TilePojo {

	private String tilePageName;
	private String tilePath;
	private String tileImage;
	private String tileImgAltText;
	private String tileCategory;
	private String tileTitle;
	private String hoverOverImg;
	private String hoverOverImgAlt;
	private List<String> tileTags;
	private String alwaysEnglish;

	public String getTileCategory() {
		return tileCategory;
	}

	public void setTileCategory(String tileCategory) {
		this.tileCategory = tileCategory;
	}

	public String getTilePageName() {
		return tilePageName;
	}

	public void setTilePageName(String tilePageName) {
		this.tilePageName = tilePageName;
	}

	public String getTilePath() {
		return tilePath;
	}

	public void setTilePath(String tilePath) {
		this.tilePath = tilePath;
	}

	public String getTileImage() {
		return tileImage;
	}

	public void setTileImage(String tileImage) {
		this.tileImage = tileImage;
	}

	public String getTileImgAltText() {
		return tileImgAltText;
	}

	public void setTileImgAltText(String tileImgAltText) {
		this.tileImgAltText = tileImgAltText;
	}

	public String getTileTitle() {
		return tileTitle;
	}

	public void setTileTitle(String tileTitle) {
		this.tileTitle = tileTitle;
	}

	public List<String> getTileTags() {
		return tileTags;
	}

	public void setTileTags(List<String> tileTags) {
		this.tileTags = tileTags;
	}

	public String getHoverOverImg() {
		return hoverOverImg;
	}

	public void setHoverOverImg(String hoverOverImg) {
		this.hoverOverImg = hoverOverImg;
	}

	public String getHoverOverImgAlt() {
		return hoverOverImgAlt;
	}

	public void setHoverOverImgAlt(String hoverOverImgAlt) {
		this.hoverOverImgAlt = hoverOverImgAlt;
	}

	@Override
	public String toString() {
		return "TilePojo tilePageName=" + tilePageName + ", tilePath=" + tilePath + ", tileImage=" + tileImage
				+ ", tileImgAltText=" + tileImgAltText + ", tileCategory=" + tileCategory + ", tileTitle=" + tileTitle
				+ ", hoverOverImg=" + hoverOverImg + ", hoverOverImgAlt=" + hoverOverImgAlt + ", tileTags=" + tileTags
				+ ", alwaysEnglish=" + alwaysEnglish;
	}

	public String getAlwaysEnglish() {
		return alwaysEnglish;
	}

	public void setAlwaysEnglish(String alwaysEnglish) {
		this.alwaysEnglish = alwaysEnglish;
	}

}
