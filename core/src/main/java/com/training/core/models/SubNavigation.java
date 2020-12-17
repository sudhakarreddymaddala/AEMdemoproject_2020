package com.training.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.training.core.helper.TrainingHelper;
import com.training.core.pojos.SubNavigationPojo;
import com.training.core.services.MultifieldReader;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Model(adaptables = Resource.class)
public class SubNavigation {
	@Inject
	@Optional
	private Node subBrandList;
	@Inject
	MultifieldReader multifieldReader;
	@Self
	Resource resource;
	private List<SubNavigationPojo> subBrandsList = new ArrayList<>();
	private static final Logger LOGGER = LoggerFactory.getLogger(SubNavigation.class);

	@PostConstruct
	protected void init() {
		LOGGER.info("Sub-Navigation Model init method ---> Start");
		if (subBrandList != null && null != resource) {
			Map<String, ValueMap> multifieldProperty;
			multifieldProperty = multifieldReader.propertyReader(subBrandList);
			if (multifieldProperty != null) {
				for (Map.Entry<String, ValueMap> entry : multifieldProperty.entrySet()) {
					SubNavigationPojo subBrandDetail = new SubNavigationPojo();
					subBrandDetail.setBrandLogoD(entry.getValue().get("brandLogoD", String.class));
					subBrandDetail.setBrandLogoAlt(entry.getValue().get("brandLogoAlt", String.class));
					subBrandDetail.setAlwaysEnglish(entry.getValue().get("alwaysEnglish",String.class));
					subBrandDetail.setBrandHoverD(entry.getValue().get("brandHoverD", String.class));
					subBrandDetail.setBrandLogoM(entry.getValue().get("brandLogoM", String.class));
					subBrandDetail.setBrandActiveM(entry.getValue().get("brandActiveM", String.class));
					subBrandDetail.setSubBrandUrl(
							TrainingHelper.checkLink(entry.getValue().get("subBrandUrl", String.class), resource));
					subBrandDetail.setBrandTarget(entry.getValue().get("brandTarget", String.class));
					subBrandsList.add(subBrandDetail);
				}
			}
		}
	}

	public List<SubNavigationPojo> getSubBrandsList() {
		return subBrandsList;
	}

	public void setSubBrandsList(List<SubNavigationPojo> subBrandsList) {
		this.subBrandsList = subBrandsList;
	}

	public void setSubBrandList(Node subBrandList) {
		this.subBrandList = subBrandList;
	}

	public void setMultifieldReader(MultifieldReader multifieldReader) {
		this.multifieldReader = multifieldReader;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}
}