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
import com.training.core.pojos.MallTourPagePojo;
import com.training.core.services.MultifieldReader;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MallTourPageModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(MallTourPageModel.class);

	@Inject
	@Optional
	private MultifieldReader multifieldReader;

	@Inject
	@Optional
	private Node mallDetailList;
	
	@Self
	Resource resource;

	List<MallTourPagePojo> mallTourPageList;

	/**
	 * The init method to fetch the tour details
	 */
	@PostConstruct
	protected void init() {
		LOGGER.info("this is calling init  method of accordian");
		if (mallDetailList != null && null!=resource) {
			Map<String, ValueMap> multifieldProperty;
			mallTourPageList = new ArrayList<>();

			multifieldProperty = multifieldReader.propertyReader(mallDetailList);
			for (Map.Entry<String, ValueMap> entry : multifieldProperty.entrySet()) {
				MallTourPagePojo mallTourPagePojo = new MallTourPagePojo();
				mallTourPagePojo.setDateDetails(entry.getValue().get("dateDetails", String.class));
				mallTourPagePojo.setLocationDetails(entry.getValue().get("locationDetails", String.class));
				mallTourPagePojo.setCtaButtonText(entry.getValue().get("ctaButtonTex", String.class));
				mallTourPagePojo.setCtaButtonUrl(TrainingHelper.checkLink(entry.getValue().get("ctaUrl", String.class),resource));
				mallTourPagePojo.setMallTourTarget(entry.getValue().get("mallTarget", String.class));
				mallTourPagePojo.setAwalysEnglish(entry.getValue().get("alwaysEnglish", String.class));

				mallTourPageList.add(mallTourPagePojo);

			}
			LOGGER.debug("mallTourPageList size is {}",mallTourPageList.size());
		}
		LOGGER.info("end init  method of accordian");
	}

	public void setMultifieldReader(MultifieldReader multifieldReader) {
		this.multifieldReader = multifieldReader;
	}

	public void setMallDetailList(Node mallDetailList) {
		this.mallDetailList = mallDetailList;
	}

	public void setMallTourPageList(List<MallTourPagePojo> mallTourPageList) {
		this.mallTourPageList = mallTourPageList;
	}

	public List<MallTourPagePojo> getMallTourPageList() {
		return mallTourPageList;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

}
