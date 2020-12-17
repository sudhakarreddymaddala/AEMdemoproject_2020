package com.training.core.models;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.training.core.helper.TrainingHelper;
import com.training.core.pojos.SiteNavigationPojo;
import com.training.core.services.TileGalleryAndLandingService;

/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Model(adaptables = { SlingHttpServletRequest.class,
		Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SiteNavigationModel {

	private static final Logger LOGGER = LoggerFactory.getLogger(SiteNavigationModel.class);
	@Inject
	private TileGalleryAndLandingService tileGalleryAndLandingService;
	

	@Self
	private SlingHttpServletRequest request;
	@Inject
	@Via("resource")
	@Optional
	private String pageUrl;
	@Inject
	@Via("resource")
	@Optional
	private Boolean collectAllPages;

	@Inject
	@Via("resource")
	@Optional
	private String brandNavUrl;
	
	@Inject
	@Via("resource")
	@Optional
	private Boolean collectRootPage;

	@Inject
	Resource resource;

	private List<SiteNavigationPojo> navItemsList = new ArrayList<>();

	/**
	 * The init method. It Will get the Child pages of Level1 and Level2 if Needed.
	 */
	@PostConstruct
	protected void init() {

		LOGGER.info("SiteNavigationModel init start");
		resource = request.getResourceResolver().getResource(pageUrl);
		try {
			Page rootPage = null;
			if (resource != null) {
				rootPage = resource.adaptTo(Page.class);
				resource.getPath();
				String currentPath = request.getPathInfo();
				Boolean checkRoot = false;
				if(collectRootPage!=null)
				{
					checkRoot = collectRootPage;
				}
				navItemsList = tileGalleryAndLandingService.getSiteNavigationDetails(rootPage, collectAllPages,
						currentPath, checkRoot);
			}

		} catch (Exception e) {
			LOGGER.error("Exception occured in init method{}" , e.getLocalizedMessage());

		}

		LOGGER.info("SiteNavigationModel init end");

	}

	public void setBrandNavUrl(String brandNavUrl) {
		this.brandNavUrl = brandNavUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public void setCollectAllPages(Boolean collectAllPages) {
		this.collectAllPages = collectAllPages;
	}

	/**
	 * @return This method return list of SiteNavigationBean
	 */
	public List<SiteNavigationPojo> getNavItemsList() {
		return navItemsList;
	}

	public String getBrandNavUrl() {
		return TrainingHelper.checkLink(brandNavUrl,resource);
	}
	
	public void setTileGalleryAndLandingService(TileGalleryAndLandingService tileGalleryAndLandingService) {
		this.tileGalleryAndLandingService = tileGalleryAndLandingService;
	}

	public void setRequest(SlingHttpServletRequest request) {
		this.request = request;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

}
