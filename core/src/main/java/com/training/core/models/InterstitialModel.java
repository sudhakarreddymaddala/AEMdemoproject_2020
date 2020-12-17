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
import com.training.core.pojos.InterstitialPojo;
import com.training.core.services.MultifieldReader;

/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Model(adaptables = Resource.class)
public class InterstitialModel {
	
	@Inject
	@Optional
	private Node interstitialDetailList;

	@Inject
	MultifieldReader multifieldReader;
	
	@Self
	Resource resource;
	
	public void setInterstitialDetailList(Node interstitialDetailList) {
		this.interstitialDetailList = interstitialDetailList;
	}
	public void setMultifieldReader(MultifieldReader multifieldReader) {
		this.multifieldReader = multifieldReader;
	}
	public void setInterstitialDetailsList(List<InterstitialPojo> interstitialDetailsList) {
		this.interstitialDetailsList = interstitialDetailsList;
	}
	private List<InterstitialPojo> interstitialDetailsList = new ArrayList<>();
	private static final Logger LOGGER = LoggerFactory.getLogger(RetailerModel.class);

	/**
	 * The init method. It Will get the Interstitital details
	 */
	@PostConstruct
	protected void init() {
		LOGGER.info("InterstitialModel init method start");
		if (interstitialDetailList != null && null!=resource) {
			Map<String, ValueMap> multifieldProperty;
			multifieldProperty = multifieldReader.propertyReader(interstitialDetailList);
			for (Map.Entry<String, ValueMap> entry : multifieldProperty.entrySet()) {
				InterstitialPojo interstitialDetail = new InterstitialPojo();
				interstitialDetail.setInterstitialLogoSrc(entry.getValue().get("interstitialLogo", String.class));
				interstitialDetail.setInterstitialLogoAlt(entry.getValue().get("interstitialLogoAlt", String.class));
				interstitialDetail.setInterstitialUrl(TrainingHelper.checkLink(entry.getValue().get("interstitialUrl", String.class),resource));
				interstitialDetail.setInterstitialTarget(entry.getValue().get("interstitialTarget",String.class));
				interstitialDetailsList.add(interstitialDetail);
			}
			LOGGER.debug("interstitialDetailsList is {}",interstitialDetailsList.size());
		}
		LOGGER.info("InterstitialModel init method end");
	}
	public List<InterstitialPojo> getInterstitialDetailsList() {
		return interstitialDetailsList;
	}
	public void setResource(Resource resource) {
		this.resource = resource;
	}


}
