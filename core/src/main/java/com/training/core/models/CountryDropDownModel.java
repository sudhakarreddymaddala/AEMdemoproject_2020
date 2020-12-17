package com.training.core.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.training.core.helper.TrainingHelper;
import com.training.core.pojos.CountryDropDownPojo;
import com.training.core.services.MultifieldReader;
import com.training.core.utils.PropertyReaderUtils;

/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Model(adaptables = { SlingHttpServletRequest.class,
		Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CountryDropDownModel {

	@Reference
	ResourceResolverFactory resourceResolverFactory;
	@RequestAttribute(name = "url")
	String pagePath;
	@SlingObject
	private SlingHttpServletRequest request;
	private static final Logger LOGGER = LoggerFactory.getLogger(CountryDropDownModel.class);
	private List<CountryDropDownPojo> countryItemsList = new ArrayList<>();
	private List<CountryDropDownPojo> countryItemsManualList = new ArrayList<>();
	private int listSize;
	String brandName;

	@Inject
	@Optional
	@Via("resource")
	private MultifieldReader multifieldReader;

	@Inject
	@Optional
	@Via("resource")
	private Node countryDetails;

	/**
	 * The init method to fetch the list of countries authored .
	 */
	@PostConstruct
	protected void init() {
		LOGGER.info("CountryDropDownModel init start");
		Resource resource = request.getResource();
		ValueMap prop = resource.adaptTo(ValueMap.class);
		if (null != prop) {
			Session session = resource.getResourceResolver().adaptTo(Session.class);
			QueryBuilder queryBuilder = resource.getResourceResolver().adaptTo(QueryBuilder.class);
			String rootPagePath = "";
			if (TrainingHelper.checkIfSiteisTraining(pagePath)) {
				rootPagePath = PropertyReaderUtils.getTrainingPath() + TrainingHelper.getBrandName(pagePath);
			} else {
				brandName = TrainingHelper.getBrandName(resource);
				int lastIndexBrand = pagePath.lastIndexOf(brandName);

				rootPagePath = pagePath.substring(0, lastIndexBrand + brandName.length());
			}
			LOGGER.debug("rootPagePath value of CountryDropDownModel is {}", rootPagePath);
			String targetValue = null != prop.get("target") ? prop.get("target").toString() : "_blank";
			LOGGER.debug("targetValue value of CountryDropDownModel is {}", targetValue);
			SearchResult result = TrainingHelper.getCountryNodesByLanguage(rootPagePath, session, queryBuilder);
			if (null != result) {
				try {
					getCountryDetails(result, resource, targetValue);
					Collections.sort(countryItemsList, (CountryDropDownPojo t1, CountryDropDownPojo t2) -> t1
							.getCountryName().toUpperCase().compareTo(t2.getCountryName().toUpperCase()));
				} catch (RepositoryException e) {
					LOGGER.error("RepositoryException occured while retriving country page list {} ", e);
				}
			}

		}
		if (countryDetails != null) {
			countryItemsManualList = getManualListOfCounties();
			LOGGER.debug("Calling Comparator for sorting");
			countryItemsManualList.sort((c1, c2) -> c1.getCountryName().compareTo(c2.getCountryName()));
		}

		LOGGER.info("CountryDropDownModel init end");
	}

	public void setResourceResolverFactory(ResourceResolverFactory resourceResolverFactory) {
		this.resourceResolverFactory = resourceResolverFactory;
	}

	public void setRequest(SlingHttpServletRequest request) {
		this.request = request;
	}

	/**
	 * @return this method will return size of list
	 */

	public int getListSize() {
		return listSize;
	}

	/**
	 * @return This method return list of Country list
	 */
	public List<CountryDropDownPojo> getCountryItemsList() {
		return countryItemsList;
	}

	/**
	 * @return This method returns the manual list of Countries
	 */
	public List<CountryDropDownPojo> getCountryItemsManualList() {
		return countryItemsManualList;
	}

	/**
	 * 
	 * @param result
	 * @param resource
	 * @param targetValue
	 * @throws RepositoryException
	 */
	private void getCountryDetails(SearchResult result, Resource resource, String targetValue)
			throws RepositoryException {
		LOGGER.info("getCountryDetails method of CountryDropDownModel start");
		String currentPageCountry = fetchCountryPath(pagePath);
		for (Hit hit : result.getHits()) {
			if (null != hit.getPath()) {
				PageManager pgmgr = resource.getResourceResolver().adaptTo(PageManager.class);
				if (null != pgmgr) {
					CountryDropDownPojo countryItem = new CountryDropDownPojo();
					String pagepath = hit.getPath().replace("jcr:content", "");
					Page page = pgmgr.getPage(pagepath);
					String pageTitle = page.getTitle();
					countryItem.setCountryName(pageTitle);
					String tempPath = TrainingHelper.checkLink(pagepath + "home", resource);
					String redirect = TrainingHelper
							.checkLink(page.getProperties().get("cq:redirectTarget", String.class), resource);
					countryItem.setCountryUrl(redirect != null ? redirect : tempPath);
					countryItem.setTarget(targetValue);
					String optionCountry = fetchCountryPath(countryItem.getCountryUrl());
					if (optionCountry.equals(currentPageCountry) && !optionCountry.equals("")) {
						countryItem.setIsCurrentCountry("true");
					} else {
						countryItem.setIsCurrentCountry("false");
					}
					countryItemsList.add(countryItem);
					LOGGER.debug("countryItemsList size of CountryDropDownModel is {}", countryItemsList.size());
				}
			}

		}
		Iterator<Resource> resources = result.getResources();
		if (resources.hasNext()) {
			ResourceResolver leakingResResolver = resources.next().getResourceResolver();
			if (leakingResResolver.isLive()) {
				leakingResResolver.close();
			}
		}
		LOGGER.info("getCountryDetails method of CountryDropDownModel end");
	}

	private List<CountryDropDownPojo> getManualListOfCounties() {
		LOGGER.info("listOfCounties Method -> Start");
		Map<String, ValueMap> brandsMap = multifieldReader.propertyReader(countryDetails);
		List<CountryDropDownPojo> countyDropdownDetails = new ArrayList<>();
		String currentPageCountry = fetchCountryPath(pagePath);
		for (Map.Entry<String, ValueMap> entry : brandsMap.entrySet()) {
			CountryDropDownPojo countryDropdownPojo = new CountryDropDownPojo();
			countryDropdownPojo.setCountryName(entry.getValue().get("countryName", String.class));
			countryDropdownPojo.setCountryUrl(entry.getValue().get("countrySiteUrl", String.class));
			String optionCountry = fetchCountryPath(entry.getValue().get("countrySiteUrl", String.class));
			if (optionCountry.equals(currentPageCountry) && !optionCountry.equals("")) {
				countryDropdownPojo.setIsCurrentCountry("true");
			} else {
				countryDropdownPojo.setIsCurrentCountry("false");
			}
			LOGGER.debug("Pojo of Country Dropdown Item {}", countryDropdownPojo);
			countyDropdownDetails.add(countryDropdownPojo);
		}
		LOGGER.info("listOfCounties Method -> End");
		return countyDropdownDetails;
	}

	private String fetchCountryPath(String currentPagePath) {
		String country = "";

		if (!currentPagePath.contains("/experience-fragments/")) {
			int indexContent = currentPagePath.indexOf("/content/");
			currentPagePath = currentPagePath.substring(indexContent + 1);
			String[] paths = currentPagePath.split("/");
			if (paths.length >= 3) {
				country = paths[2];
			}
		}
		return country;
	}

	public void setPagePath(String pagePath) {
		this.pagePath = pagePath;
	}

}