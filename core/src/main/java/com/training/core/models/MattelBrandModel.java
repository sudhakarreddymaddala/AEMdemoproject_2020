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
import com.training.core.pojos.MattelBrandsPojo;
import com.training.core.services.MultifieldReader;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MattelBrandModel {

	@Inject
	@Optional
	private Node brandDetails;

	@Inject
	private MultifieldReader multifieldReader;

	@Self
	Resource resource;

	List<MattelBrandsPojo> brandsList = new ArrayList<>();

	private static final Logger LOGGER = LoggerFactory.getLogger(MattelBrandModel.class);

	/**
	 * The init method to to fetch the header Links
	 */
	@PostConstruct
	public void init() {
		LOGGER.info("Mattel Model init method start");
		Map<String, ValueMap> multifieldProperty;

		if (brandDetails != null && null != resource) {

			multifieldProperty = multifieldReader.propertyReader(brandDetails);

			for (Map.Entry<String, ValueMap> entry : multifieldProperty.entrySet()) {

				MattelBrandsPojo mainBrandLinks = new MattelBrandsPojo();

				mainBrandLinks
						.setLogoURL(TrainingHelper.checkLink(entry.getValue().get("logoURL", String.class), resource));
				mainBrandLinks.setTargetURL(entry.getValue().get("targetURL", String.class));
				mainBrandLinks.setLogoAltText(entry.getValue().get("logoAltText", String.class));
				mainBrandLinks.setLogoImage(entry.getValue().get("logoImage", String.class));
				mainBrandLinks.setAlwaysEnglish(entry.getValue().get("alwaysEnglish", String.class));

				brandsList.add(mainBrandLinks);
			}

			LOGGER.info("M init method end");
		}
	}

	public List<MattelBrandsPojo> getBrandsList() {
		return brandsList;
	}

	public void setBrandsList(List<MattelBrandsPojo> brandsList) {
		this.brandsList = brandsList;
	}

	public void setBrandDetails(Node brandDetails) {
		this.brandDetails = brandDetails;
	}

	public void setMultifieldReader(MultifieldReader multifieldReader) {
		this.multifieldReader = multifieldReader;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

}
