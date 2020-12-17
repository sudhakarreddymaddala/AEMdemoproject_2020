package com.training.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
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

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.training.core.helper.TrainingHelper;
import com.training.core.pojos.ProductTilePojo;
import com.training.core.services.MultifieldReader;
import com.training.core.services.ProductGalleryAndLandingService;
import com.training.core.utils.PropertyReaderUtils;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Component(service = Servlet.class, immediate = true, property = {
		Constants.SERVICE_DESCRIPTION + "=Product Landing Json Creation Servlet",
		"sling.servlet.paths=" + "/bin/getProductLandingGrid" })
public class ProductLandingServlet extends SlingSafeMethodsServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductLandingServlet.class);
	private transient List<ProductTilePojo> productsList = new ArrayList<>();
	@Inject
	MultifieldReader multifieldReader;
	@Reference
	private transient ProductGalleryAndLandingService productGalleryAndLandingService;
	String orderProduct;
	String columnConfig;
	String[] productItems;
	String lazyLoadLimit;
	String titleAlign = "";
	String tileType = "products";
	String tilePage = "landing";
	String allLabelText;
	String alwaysEnglish;
	String sectionAltTitle = "";
	String altTitle = "sectionAltTitle";
	String ef = "experiencefragment";
	String aligntitle = "titleAlign";
	String expFragmentResource = "cq/experience-fragments/editor/components/experiencefragment";
	String slingResourceType = "sling:resourceType";
	String productDetailPage = "mattel/play/components/structure/productdetail-page";
	String frgamentPath = "fragmentPath";
	String productThumbnailGridNode;
	boolean linkToPages = true;

	/**
	 * The doGet Method to fetch the List of Product Details and Product Landing
	 * Grid Properties
	 */
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		LOGGER.info("Start of doGet method ProductLandingServlet");
		String currentPagePath = request.getParameter("currentPagePath");
		LOGGER.debug("currentPagePath value of ProductLandingServlet is {}",currentPagePath);
		ResourceResolver resolver;

		try {

			resolver = request.getResourceResolver();
			Resource pageResource = resolver.getResource(currentPagePath);

			if (pageResource != null) {
				PageManager pageManager = resolver.adaptTo(PageManager.class);
				if (pageManager != null) {
					Page page = pageManager.getContainingPage(pageResource);
					Page homePage = page.getAbsoluteParent(5);
					Tag categoryId = null;
					if (null != request.getParameter("category")) {
						TagManager tagMag = resolver.adaptTo(TagManager.class);
						if (null != tagMag) {
							categoryId = tagMag.resolve(request.getParameter("category"));
						}
					}
					fetchTagDetailsAndResponse(categoryId, page, currentPagePath, resolver, homePage, response);

				}
			}

		} catch (NullPointerException e) {
			LOGGER.error("Null PointerException Occured {} ", e);
		} catch (JSONException e) {
			LOGGER.error("JSON Exception Occured {} ", e);
		}
		LOGGER.info("End of doGet method ProductLandingServlet");
	}

	/**
	 * Method to fetch the Page Tag Details and Response
	 * 
	 * @param categoryId
	 * @param page
	 * @param currentPagePath
	 * @param resolver
	 * @param homePage
	 * @param response
	 * @throws JSONException
	 * @throws IOException
	 */
	private void fetchTagDetailsAndResponse(Tag categoryId, Page page, String currentPagePath,
			ResourceResolver resolver, Page homePage, SlingHttpServletResponse response)
			throws JSONException, IOException {
		List<Tag> categoryTags = new ArrayList<>();
		if (null != categoryId) {
			categoryTags.add(categoryId);
		} else {
			categoryTags = Arrays.asList(page.getTags());
		}
		int tagLength = categoryTags.size();
		LOGGER.debug("tagLength value of ProductLandingServlet is {}",tagLength);
		String pageResType = "";
		pageResType = page.getContentResource().getResourceType();
		LOGGER.debug("pageResType value of ProductLandingServlet is {}",pageResType);
		fetchProductThumbnailGridNodePath(tagLength, page, currentPagePath, resolver, pageResType);
		if (productThumbnailGridNode != null) {
			fetchNodeProperties(resolver);
			getProductList(homePage);
			if (tagLength != 0) {
				productsList = filterCategory(categoryTags, tagLength);
			}
			prepareJsonResponse(response);
		}

	}

	/**
	 * Method to fetch the Product Thumb nail Grid Node Path
	 * 
	 * @param tagLength
	 * @param page
	 * @param currentPath
	 * @param resolver
	 * @param pageResType
	 */
	private void fetchProductThumbnailGridNodePath(int tagLength, Page page, String currentPath,
			ResourceResolver resolver, String pageResType) {
		LOGGER.info("Start of fetchProductThumbnailGridNodePath method");
		if (tagLength != 0 || productDetailPage.equalsIgnoreCase(pageResType)) {
			Page parentPage = page.getParent();
			if (null != parentPage && StringUtils.isNoneEmpty(parentPage.getPath())) {
				Resource currPageRootRes = resolver.getResource(
						parentPage.getPath() + com.training.core.constants.Constants.JCR_CONTENT_ROOT_WOSLASH);
				productThumbnailGridNode = TrainingHelper.checkProductThumbnailExpFragemnt(resolver, currPageRootRes);
			}
		} else {
			Resource currPageRootRes = resolver
					.getResource(currentPath + com.training.core.constants.Constants.JCR_CONTENT_ROOT_WOSLASH);
			productThumbnailGridNode = TrainingHelper.checkProductThumbnailExpFragemnt(resolver, currPageRootRes);
		}
		LOGGER.info("End of fetchProductThumbnailGridNodePath method");
	}

	/**
	 * Method to fetch the Product Landing Grid Node Path
	 * 
	 * @param resolver
	 */
	private void fetchNodeProperties(ResourceResolver resolver) {
		Resource landingNodeResource = resolver.getResource(productThumbnailGridNode);
		if (landingNodeResource != null) {
			ValueMap nodeValues = landingNodeResource.adaptTo(ValueMap.class);
			if (null != nodeValues) {
				orderProduct = nodeValues.get("orderProduct", String.class);
				columnConfig = null != nodeValues.get("columnConfig", String.class)
						? nodeValues.get("columnConfig", String.class)
						: "column-3";
				lazyLoadLimit = nodeValues.get("displayProducts", String.class);
				allLabelText = nodeValues.get("allLabel", String.class);
				productItems = nodeValues.get("pages", String[].class);
				titleAlign = nodeValues.get(aligntitle, String.class);
				sectionAltTitle = nodeValues.get(altTitle, String.class);
				alwaysEnglish = nodeValues.get("alwaysEnglish", String.class);
				if(null!=nodeValues.get("linktoPages", Boolean.class)){
					linkToPages = nodeValues.get("linktoPages", Boolean.class);
				}
			}
		}
	}

	/**
	 * Method to fetch the Category Details
	 * 
	 * @param categoryTags
	 * @param tagLength
	 * @return
	 */
	private List<ProductTilePojo> filterCategory(List<Tag> categoryTags, int tagLength) {
		List<ProductTilePojo> tempList = new ArrayList<>();
		if (tagLength != 0) {
			for (ProductTilePojo tilePojo : productsList) {
				List<String> tagList = tilePojo.getProductTags();
				if (tagList != null) {
					checkProductTagMatch(categoryTags, tagList, tilePojo, tempList);
				}
			}
		}
		return tempList;
	}

	/**
	 * Method to Check the Product tag Match
	 * 
	 * @param categoryTags
	 * @param tagList
	 * @param tilePojo
	 * @param tempList
	 * @return
	 */
	private List<ProductTilePojo> checkProductTagMatch(List<Tag> categoryTags, List<String> tagList,
			ProductTilePojo tilePojo, List<ProductTilePojo> tempList) {
		boolean tagMatch = false;
		for (Tag catTagId : categoryTags) {
			if (tagList.contains(catTagId.getTagID()))
				tagMatch = true;
		}
		if (tagMatch) {
			tempList.add(tilePojo);
		}
		return tempList;

	}

	/**
	 * Method to Fetch the Products List
	 * 
	 * @param homePage
	 */
	private void getProductList(Page homePage) {
		LOGGER.info("Start of getProductList method");
		if (null != orderProduct) {
			productsList.clear();
			if (orderProduct.equals("manual")) {
				productsList = productGalleryAndLandingService.getAllTiles(productItems,linkToPages);
				LOGGER.debug("productsList size of ProductLandingServlet is {}",productsList.size());
			} else if (orderProduct.equals("automatic")) {
				String currentPath = homePage.getPath() + PropertyReaderUtils.getProductPath();
				LOGGER.debug("currentPath value of ProductLandingServlet is {}",currentPath);
				productsList = productGalleryAndLandingService.getTilesByDate(currentPath,linkToPages);
				LOGGER.debug("productsList size of ProductLandingServlet is {}",productsList.size());
			}
		}
		LOGGER.info("Start of getProductList method");
	}

	/**
	 * Method to Prepare the JSON Response
	 * 
	 * @param response
	 * @throws JSONException
	 * @throws IOException
	 */
	private void prepareJsonResponse(SlingHttpServletResponse response) throws JSONException, IOException {
		LOGGER.info("Json Conversion");
		org.json.JSONObject obj = new org.json.JSONObject();
		obj.put("lazyLoadLimit", lazyLoadLimit);
		obj.put("relproductsTitle", "NEWEST Products");
		obj.put("colLayout", columnConfig);
		obj.put("alwaysEnglish", alwaysEnglish);

		obj.put(tileType, productsList);

		if (titleAlign == null) {
			obj.put(aligntitle, "center");
		} else {
			obj.put(aligntitle, titleAlign);
		}
		if (sectionAltTitle == null) {
			obj.put(altTitle, "Play More Products");
		} else {
			obj.put(altTitle, sectionAltTitle);
		}
		response.setContentType("application/json");
		response.getWriter().print(obj);
		LOGGER.info("ProductLandingServlet: doGet end");
	}

	public void setProductGalleryAndLandingService(ProductGalleryAndLandingService productGalleryAndLandingService) {
		this.productGalleryAndLandingService = productGalleryAndLandingService;
	}

	public void setProductThumbnailGridNode(String productThumbnailGridNode) {
		this.productThumbnailGridNode = productThumbnailGridNode;
	}

}
