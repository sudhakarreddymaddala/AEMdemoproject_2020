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

import com.training.core.pojos.ParentFooterPojo;
import com.training.core.pojos.SocialIconsPojo;
import com.training.core.services.MultifieldReader;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class ParentFooterModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(ParentFooterModel.class);

	@Self
	Resource resource; 

	
	@Inject
	@Optional
	private Node footerLinksOne;
	
	@Inject
	@Optional
	private Node socialIcons;

	@Inject
	@Optional
	private Node footerLinksTwo;

	@Inject
	@Optional
	private Node footerLinksThree;

	@Inject
	@Optional
	private Node footerLinksFour;

	@Inject
	private MultifieldReader multifieldReader;
	
	String linkText = "linkText";
	String linkTarget= "linkTarget";
	String alwaysEnglish= "alwaysEnglish";
	String linkURL = "linkURL";

	
    

	private  List<ParentFooterPojo> footerTextGroupOne = new ArrayList<>();
	private  List<ParentFooterPojo> footerTextGroupTwo = new ArrayList<>();
	private  List<ParentFooterPojo> footerTextGroupThree = new ArrayList<>();
	private  List<ParentFooterPojo> footerTextGroupFour = new ArrayList<>();
	
	private  List<SocialIconsPojo> socialIconsList = new ArrayList<>();

	

	

	/**
	 * The init method to fetch the list of footer link details.
	 */
	
	@PostConstruct
	public void init () {

		if (null != resource) {
			fetchFooterLinksByNodes();

		}
		
		if (socialIcons != null && null != resource) {
			Map<String, ValueMap> multifieldProperty;
			multifieldProperty = multifieldReader.propertyReader(socialIcons);
			if (multifieldProperty != null) {
				for (Map.Entry<String, ValueMap> entry : multifieldProperty.entrySet()) {
					SocialIconsPojo socialIconsDetail = new SocialIconsPojo();
					socialIconsDetail.setIcons(entry.getValue().get("icons", String.class));
					socialIconsDetail.setLinkText(entry.getValue().get(linkText, String.class));
					socialIconsDetail.setLinkTarget(entry.getValue().get(linkTarget, String.class));
					socialIconsDetail.setAlwaysEnglish(entry.getValue().get(alwaysEnglish, String.class));
					socialIconsDetail.setSocialLinkURL(
							TrainingHelper.checkLink(entry.getValue().get("socialLinkURL", String.class), resource));
					
					socialIconsList.add(socialIconsDetail);
				}
			}
		}
	}

	private void fetchFooterLinksByNodes() {
		if (null != footerLinksOne) {
		footerTextGroupOne = fetchFooterLinks(footerLinksOne);
		}
		if (null != footerLinksTwo) {
		footerTextGroupTwo = fetchFooterLinks(footerLinksTwo);
		}
		if (null != footerLinksThree) {
		footerTextGroupThree = fetchFooterLinks(footerLinksThree);
		}
		if (null != footerLinksFour) {
		footerTextGroupFour = fetchFooterLinks(footerLinksFour);
		}
	}

	private List<ParentFooterPojo> fetchFooterLinks(Node footerLinkNode) {
		Map<String, ValueMap> multifieldProperty;
		List<ParentFooterPojo> groupLinks = new ArrayList<>();
		multifieldProperty = multifieldReader.propertyReader(footerLinkNode);

		for (Map.Entry<String, ValueMap> entry : multifieldProperty.entrySet()) {
			ParentFooterPojo footerLink = new ParentFooterPojo();
			footerLink.setLinkText(entry.getValue().get(linkText, String.class));
			LOGGER.info("footerrrrr ---> End {}", entry.getValue().get(linkText, String.class));
			footerLink.setLinkURL(TrainingHelper.checkLink(entry.getValue().get(linkURL, String.class), resource));
			LOGGER.info("linkURL ---> End {}", entry.getValue().get(linkURL, String.class));
			footerLink.setLinkTarget(entry.getValue().get(linkTarget, String.class));
			LOGGER.info("linkTarget ---> End {}", entry.getValue().get(linkTarget, String.class));
			footerLink.setAlwaysEnglish(entry.getValue().get(alwaysEnglish, String.class));
			LOGGER.info("alwaysEnglish ---> End {}", entry.getValue().get(alwaysEnglish, String.class));
			groupLinks.add(footerLink);
		}
		return groupLinks;

	}

	

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public List<ParentFooterPojo> getFooterTextGroupOne() {
		return footerTextGroupOne;
	}

	public void setFooterTextGroupOne(List<ParentFooterPojo> footerTextGroupOne) {
		this.footerTextGroupOne = footerTextGroupOne;
	}

	public List<ParentFooterPojo> getFooterTextGroupTwo() {
		return footerTextGroupTwo;
	}

	public void setFooterTextGroupTwo(List<ParentFooterPojo> footerTextGroupTwo) {
		this.footerTextGroupTwo = footerTextGroupTwo;
	}

	public List<ParentFooterPojo> getFooterTextGroupThree() {
		return footerTextGroupThree;
	}

	public void setFooterTextGroupThree(List<ParentFooterPojo> footerTextGroupThree) {
		this.footerTextGroupThree = footerTextGroupThree;
	}

	public List<ParentFooterPojo> getFooterTextGroupFour() {
		return footerTextGroupFour;
	}

	public void setFooterTextGroupFour(List<ParentFooterPojo> footerTextGroupFour) {
		this.footerTextGroupFour = footerTextGroupFour;
	}

	public List<SocialIconsPojo> getSocialIconsList() {
		return socialIconsList;
	}

	public void setSocialIconsList(List<SocialIconsPojo> socialIconsList) {
		this.socialIconsList = socialIconsList;
	}

	public void setSocialIcons(Node socialIcons) {
		this.socialIcons = socialIcons;
	}

	public void setFooterLinksOne(Node footerLinksOne) {
		this.footerLinksOne = footerLinksOne;
	}

	public void setFooterLinksTwo(Node footerLinksTwo) {
		this.footerLinksTwo = footerLinksTwo;
	}

	public void setFooterLinksThree(Node footerLinksThree) {
		this.footerLinksThree = footerLinksThree;
	}

	public void setFooterLinksFour(Node footerLinksFour) {
		this.footerLinksFour = footerLinksFour;
	}
	public void setMultifieldReader(MultifieldReader multifieldReader) {
		this.multifieldReader = multifieldReader;
	}
}
