package com.training.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONException;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.training.core.pojos.RetailerPojo;
import com.training.core.services.MultifieldReader;
import com.training.core.services.TileGalleryAndLandingService;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Component(service = Servlet.class, immediate = true, property = {
		Constants.SERVICE_DESCRIPTION + "=Download Interstitial App Json Creation Servlet",
		"sling.servlet.paths=" + "/bin/getRetailerList" })
public class RetailerInterstitialServlet extends SlingSafeMethodsServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadInterstitialAppServlet.class);
	private transient List<RetailerPojo> retailerDetailsList = new ArrayList<>();
	@Reference
	private transient MultifieldReader multifieldReader;
	@Reference
	private transient TileGalleryAndLandingService tileGalleryAndLandingService;
	Resource retailerResource;

	/**
	 * The doGet Method to fetch the Product specific Retailer List
	 */
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		LOGGER.info("doGet method of RetailerInterstitialServlet started");
		String pagePath = request.getParameter("pagePath");
		pagePath = checkPagePath(pagePath);
		LOGGER.debug("pagePath value of RetailerInterstitialServlet is {}",pagePath);
		ResourceResolver resolver;
		try {
			resolver = request.getResourceResolver();
			Resource currentResource = resolver.getResource(pagePath);
			PageManager pageManager = resolver.adaptTo(PageManager.class);
			if(null!=pageManager){
				Page currentPage = pageManager.getContainingPage(currentResource);
				Page homePage = currentPage.getAbsoluteParent(5);
				String homePagePath = homePage.getPath();
				LOGGER.debug("homePagePath value is {}",homePagePath);
				fetchRetailNode(homePagePath, pagePath, request);

				if (retailerResource != null) {
					Node retailerNode = retailerResource.adaptTo(Node.class);
					if (retailerNode != null) {
						Map<String, ValueMap> multifieldProperty;
						multifieldProperty = multifieldReader.propertyReader(retailerNode);
						if (multifieldProperty != null) {
							retailerDetailsList = tileGalleryAndLandingService.fetchRetailerDetails(multifieldProperty,retailerResource);
						}
						if (retailerDetailsList != null) {
							prepareJsonResponse(response);
						}
					}
				}
			}
		} catch (JSONException e) {
			LOGGER.error("JSON Exception Occured {} ", e);
		}
		LOGGER.info("doGet method of RetailerInterstitialServlet end");
	}

	/**
	 * Method to Prepare the JSON Respopnse
	 * 
	 * @param response
	 * @throws JSONException
	 * @throws IOException
	 */
	private void prepareJsonResponse(SlingHttpServletResponse response) throws JSONException, IOException {
		LOGGER.info("Json Conversion");
		org.json.JSONObject obj = new org.json.JSONObject();
		obj.put("retailers", retailerDetailsList);
		response.setContentType("application/json");
		response.getWriter().print(obj);
		LOGGER.info("EventCreationServlet: doGet end");
	}

	/**
	 * Method to check the path for '.html' extension
	 * 
	 * @param pagePath
	 * @return
	 */
	private String checkPagePath(String pagePath) {
		if (pagePath.contains(".html")) {
			String pageUrl = pagePath.replace(".html", "");
			LOGGER.debug("pageUrl value of RetailerInterstitialServlet is {}",pageUrl);
			pagePath = pageUrl;
		}
		return pagePath;
	}

	private void fetchRetailNode(String homePagePath, String pagePath, SlingHttpServletRequest request) {
		String tempPath = "";
		if (pagePath.contains("jcr:content")) {
			String[] pageSplits = pagePath.split("/jcr:content");
			if (pageSplits.length != 0 && pageSplits[0] != null) {
				tempPath = pageSplits[0];
				LOGGER.debug("tempPath value of RetailerInterstitialServlet is {}",tempPath);
			}
		}
		if (homePagePath.equals(tempPath)) {
			retailerResource = request.getResourceResolver()
					.getResource(pagePath + com.training.core.constants.Constants.RETAILER_NODE);
		} else {
			retailerResource = request.getResourceResolver()
					.getResource(pagePath + com.training.core.constants.Constants.SLASH_JCR_CONTENT
							+ com.training.core.constants.Constants.PRODUCT_RETAILER_NODE);
		}

	}

	public void setRetailerDetailsList(List<RetailerPojo> retailerDetailsList) {
		this.retailerDetailsList = retailerDetailsList;
	}

	public void setMultifieldReader(MultifieldReader multifieldReader) {
		this.multifieldReader = multifieldReader;
	}

	public void setTileGalleryAndLandingService(TileGalleryAndLandingService tileGalleryAndLandingService) {
		this.tileGalleryAndLandingService = tileGalleryAndLandingService;
	}

	public void setRetailerResource(Resource retailerResource) {
		this.retailerResource = retailerResource;
	}
	
}
