package com.training.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.training.core.helper.TrainingHelper;
import com.training.core.pojos.HeaderPojo;
import com.training.core.services.MultifieldReader;

/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderModel {

	@Inject
	@Optional
	private Node mainNavDetail;

	@Inject
	@Optional
	private String globalUrl;

	@Inject
	private MultifieldReader multifieldReader;
	
	@Self
	Resource resource;

	private static List<HeaderPojo> mainNavList = new ArrayList<>();

	public void setMainNavDetail(Node mainNavDetail) {
		this.mainNavDetail = mainNavDetail;
	}

	public void setMultifieldReader(MultifieldReader multifieldReader) {
		this.multifieldReader = multifieldReader;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(HeaderModel.class);

	/**
	 * The init method to to fetch the header Links
	 */
	@PostConstruct
	public void init() {
		LOGGER.info("HeaderModel init method start");
		Map<String, ValueMap> multifieldProperty;

		if (mainNavDetail != null && null!=resource) {
			multifieldProperty = multifieldReader.propertyReader(mainNavDetail);
			mainNavList.clear();
			for (Map.Entry<String, ValueMap> entry : multifieldProperty.entrySet()) {

				HeaderPojo mainNavLinks = new HeaderPojo();

				mainNavLinks.setNavLabel(entry.getValue().get("navLabel", String.class));
				mainNavLinks.setNavLink(TrainingHelper.checkLink(entry.getValue().get("navLink", String.class),resource));
				mainNavLinks.setNavTarget(entry.getValue().get("navTarget", String.class));
				mainNavLinks.setNavAltText(entry.getValue().get("navAltText", String.class));
				mainNavLinks.setNavImage(entry.getValue().get("navImage", String.class));
				mainNavLinks.setAlwaysEnglish(entry.getValue().get("alwaysEnglish", String.class));
				mainNavLinks.setNavDesktopHoverImage(entry.getValue().get("navDesktopHoverImage", String.class));
				mainNavLinks.setNavMobileImage(entry.getValue().get("navMobileImage", String.class));

				mainNavList.add(mainNavLinks);
			}
			LOGGER.debug("mainNavLinks size is {}",mainNavList.size());
			LOGGER.info("HeaderModel init method end");
		}
	}

	public List<HeaderPojo> getMainNavList() {

		return mainNavList;
	}

	public String getGlobalUrl() {
		return TrainingHelper.checkLink(globalUrl,resource);
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

}
