package com.training.core.pojos;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
public class ArticlePojo {
	private String pageTitile;
	private String pageDescription;
	private String articlePath;
	private String articleThumnnail;
	private String articleTagName;
	private String alwaysEnglish;
	private String articleTagNameLowercase;
	
	public String getPageTitile() {
		return pageTitile;
	}
	public void setPageTitile(String pageTitile) {
		this.pageTitile = pageTitile;
	}
	public String getPageDescription() {
		return pageDescription;
	}
	public void setPageDescription(String pageDescription) {
		this.pageDescription = pageDescription;
	}
	public String getArticleThumnnail() {
		return articleThumnnail;
	}
	public void setArticleThumnnail(String articleThumnnail) {
		this.articleThumnnail = articleThumnnail;
	}
	public String getArticleTagName() {
		return articleTagName;
	}
	public void setArticleTagName(String articleTagName) {
		this.articleTagName = articleTagName;
	}
	public String getAlwaysEnglish() {
		return alwaysEnglish;
	}
	public void setAlwaysEnglish(String alwaysEnglish) {
		this.alwaysEnglish = alwaysEnglish;
	}
	public String getArticlePath() {
		return articlePath;
	}
	public void setArticlePath(String articlePath) {
		this.articlePath = articlePath;
	}

	public String getArticleTagNameLowercase() {
		return articleTagNameLowercase;
	}
	public void setArticleTagNameLowercase(String articleTagNameLowercase) {
		this.articleTagNameLowercase = articleTagNameLowercase;
	}
	@Override
	public String toString() {
		return "ArticlePojo [pageTitile=" + pageTitile + ", pageDescription=" + pageDescription + ", articlePath="
				+ articlePath + ", articleThumnnail=" + articleThumnnail + ", articleTagName=" + articleTagName
				+ ", alwaysEnglish=" + alwaysEnglish + ", articleTagNameLowercase=" + articleTagNameLowercase + "]";
	}
	
}
