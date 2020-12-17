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

import com.training.core.pojos.RetailerPojo;
import com.training.core.services.MultifieldReader;
import com.training.core.services.TileGalleryAndLandingService;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */

@Model(adaptables = Resource.class)
public class RetailerModel {
	@Inject
	@Optional
	private Node retailerDetailList;
	@Inject
	MultifieldReader multifieldReader;
	@Inject
	TileGalleryAndLandingService tileGalleryAndLandingService;
	@Self
	Resource resource;
	private List<RetailerPojo> retailerDetailsList = new ArrayList<>();
	private static final Logger LOGGER = LoggerFactory.getLogger(RetailerModel.class);
	
	private int listLength = 0 ;

	/**
	 * The init method to fetch the list of Retailer Details.
	 */
	@PostConstruct
	protected void init() {
		LOGGER.info("RerailerModel init method ---> Start");
		if (retailerDetailList != null && null!=resource) {
			Map<String, ValueMap> multifieldProperty;
			multifieldProperty = multifieldReader.propertyReader(retailerDetailList);
			if (multifieldProperty != null) {
				retailerDetailsList = tileGalleryAndLandingService.fetchRetailerDetails(multifieldProperty,resource);
			}			
		}
		listLength = retailerDetailsList.size();
		LOGGER.info("RerailerModel init method ---> End");
	}

	public int getListLength() {
		return listLength;
	}

	public void setRetailerDetailList(Node retailerDetailList) {
		this.retailerDetailList = retailerDetailList;
	}

	public void setMultifieldReader(MultifieldReader multifieldReader) {
		this.multifieldReader = multifieldReader;
	}

	public void setTileGalleryAndLandingService(TileGalleryAndLandingService tileGalleryAndLandingService) {
		this.tileGalleryAndLandingService = tileGalleryAndLandingService;
	}

	public void setRetailerDetailsList(List<RetailerPojo> retailerDetailsList) {
		this.retailerDetailsList = retailerDetailsList;
	}

	public List<RetailerPojo> getRetailerDetailsList() {
		return retailerDetailsList;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

}
