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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.training.core.pojos.AccordionPojo;
import com.training.core.services.MultifieldReader;

/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AccordionModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(AccordionModel.class);

	@Inject
	@Optional
	private MultifieldReader multifieldReader;

	@Inject
	@Optional
	private Node accordian;

	List<AccordionPojo> accordianList;

	@PostConstruct
	protected void init() {
		LOGGER.info("this is calling init  method of accordian start");
		if (accordian != null) {
			Map<String, ValueMap> multifieldProperty;
			accordianList = new ArrayList<>();

			multifieldProperty = multifieldReader.propertyReader(accordian);
			for (Map.Entry<String, ValueMap> entry : multifieldProperty.entrySet()) {

				AccordionPojo accordiaPojo = new AccordionPojo();
				accordiaPojo.setContentTitle(entry.getValue().get("contentTitle", String.class));
				accordiaPojo.setContentDescription(entry.getValue().get("contentDescription", String.class));
				accordiaPojo.setAwalysEnglish(entry.getValue().get("awalysEnglish", String.class));
				accordianList.add(accordiaPojo);
			}
			LOGGER.debug("accordianList of accordianis {}", accordianList.size());
		}
		LOGGER.info("this is ending the AccordianModel init");

	}

	public void setAccordian(Node accordian) {
		this.accordian = accordian;
	}

	public void setMultifieldReader(MultifieldReader multifieldReader) {
		this.multifieldReader = multifieldReader;
	}

	public List<AccordionPojo> getAccordianList() {
		return accordianList;
	}

}
