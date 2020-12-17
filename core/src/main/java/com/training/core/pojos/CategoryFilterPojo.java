package com.training.core.pojos;

/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
import java.util.List;

public class CategoryFilterPojo {
	private String categoryTitle;
	private List<String> categoryTag;
	private String categoryPath;

	public String getCategoryPath() {
		return categoryPath;
	}

	public void setCategoryPath(String categoryPath) {
		this.categoryPath = categoryPath;
	}

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

}
