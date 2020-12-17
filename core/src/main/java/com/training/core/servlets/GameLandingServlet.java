package com.training.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
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

import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.training.core.pojos.CategoryFilterPojo;
import com.training.core.pojos.TilePojo;
import com.training.core.services.TileGalleryAndLandingService;
import com.training.core.utils.PropertyReaderUtils;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Component(service = Servlet.class, immediate = true, property = {
		Constants.SERVICE_DESCRIPTION + "=Game Landing Json Creation Servlet",
		"sling.servlet.paths=" + "/bin/getGameLandingGrid" })
public class GameLandingServlet extends SlingSafeMethodsServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(GameLandingServlet.class);
	private transient List<TilePojo> tileList = new ArrayList<>();
	private static List<CategoryFilterPojo> categoryList = new ArrayList<>();
	@Reference
	private transient TileGalleryAndLandingService tileGalleryAndLandingService;
	String orderGame;
	String lazyLoadLimit;
	String columnLayout;
	String titleAlign = "";
	String tileType = "games";
	String tilePage = "landing";
	String allLabelText;
	String sectionAltTitle = "";
	String altTitle = "sectionAltTitle";
	String ef = "experiencefragment";
	String aligntitle = "titleAlign";
	String rootPath;

	/**
	 * The doGet Method to fetch the List of Games and Game Landing Grid Properties
	 */
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		LOGGER.info("doGet method of GameLandingServlet started");
		Resource resource;
		String currentPath = request.getParameter("currentPath");
		LOGGER.debug("currentPath value of GameLandingServlet is {}",currentPath);
		String currentPagePath = request.getParameter("currentPagePath");
		LOGGER.debug("currentPagePath value of GameLandingServlet is {}",currentPagePath);
		ResourceResolver resolver;
		try {
			resolver = request.getResourceResolver();
			resource = resolver.getResource(currentPath);
			Resource currentResource = resolver.getResource(currentPagePath);
			PageManager pageManager = resolver.adaptTo(PageManager.class);
			Page currentPage = pageManager.getContainingPage(currentResource);
			Page homePage = currentPage.getAbsoluteParent(5);
			ValueMap nodeValues = resource.adaptTo(ValueMap.class);
			if (null != nodeValues) {
				orderGame = nodeValues.get("orderGame", String.class);
				LOGGER.debug("orderGame value of GameLandingServlet is {}",orderGame);
				lazyLoadLimit = nodeValues.get("displayGames", String.class);
				LOGGER.debug("lazyLoadLimit value of GameLandingServlet is {}",lazyLoadLimit);
				allLabelText = nodeValues.get("allLabel", String.class);
				LOGGER.debug("allLabelText value of GameLandingServlet is {}",allLabelText);
				titleAlign = nodeValues.get(aligntitle, String.class);
				LOGGER.debug("titleAlign value of GameLandingServlet is {}",titleAlign);
				sectionAltTitle = nodeValues.get(altTitle, String.class);
				LOGGER.debug("sectionAltTitle value of GameLandingServlet is {}",sectionAltTitle);
				columnLayout = nodeValues.get("columnLayout", String.class);
				LOGGER.debug("columnLayout value of GameLandingServlet is {}",columnLayout);
				if (orderGame != null) {
					tileList.clear();
					categoryList.clear();
					if (orderGame.equals("manual")) {
						tileList = tileGalleryAndLandingService.getAllTiles(currentPath, tileType, tilePage, true);
						LOGGER.debug("tileList size of GameLandingServlet is {}",tileList.size());
					} else if (orderGame.equals("automatic")) {
						currentPath = homePage.getPath() + PropertyReaderUtils.getGamePath();
						tileList = tileGalleryAndLandingService.getTilesByDate(currentPath, tileType, null, resolver,
								true);
						LOGGER.debug("tileList size of GameLandingServlet is {}",tileList.size());
					}
				}
				Node landingGrid = resource.adaptTo(Node.class);
				if (landingGrid != null && landingGrid.hasNode("categoryDetail")) {
					Node categoryDetail = landingGrid.getNode("categoryDetail");
					fetchCategoryList(categoryDetail, pageManager);
				}
				prepareJsonResponse(response);

			}

		} catch (NullPointerException e) {
			LOGGER.error("Null PointerException Occured {} ", e);
		} catch (JSONException e) {
			LOGGER.error("JSON Exception Occured {} ", e);
		} catch (RepositoryException e) {
			LOGGER.error("RepositoryException Exception Occured {} ", e);
		}
		LOGGER.info("doGet method of GameLandingServlet end");
	}

	/**
	 * Method to fetch the Game Categories
	 * 
	 * @param categoryDetail
	 * @param pageManager
	 * @throws RepositoryException
	 */
	private void fetchCategoryList(Node categoryDetail, PageManager pageManager) throws RepositoryException {
		NodeIterator categories = categoryDetail.getNodes();
		if (categories.hasNext()) {
			if (allLabelText != null) {
				CategoryFilterPojo allLabelPojo = new CategoryFilterPojo();
				allLabelPojo.setCategoryTitle(allLabelText);
				List<String> allLabel = new ArrayList<>();
				allLabel.add("All");
				allLabelPojo.setCategoryTag(allLabel);
				categoryList.add(allLabelPojo);
			}
			while (categories.hasNext()) {
				Node category = categories.nextNode();
				CategoryFilterPojo catPojo = new CategoryFilterPojo();
				String categoryPath = category.getProperty("category") != null
						? category.getProperty("category").getString()
						: "";
				if (!categoryPath.isEmpty()) {
					Page categoryPage = pageManager.getPage(categoryPath);
					String categoryTitle = categoryPage.getTitle();
					catPojo.setCategoryTitle(categoryTitle);
					List<String> tags = new ArrayList<>();
					Tag[] categoryTag = categoryPage.getTags();
					for (Tag tag : categoryTag) {
						tags.add(tag.getTagID());
					}
					catPojo.setCategoryTag(tags);
				}
				categoryList.add(catPojo);
			}
		}

	}

	/**
	 * Method to Prepare JSON Response
	 * 
	 * @param response
	 * @throws JSONException
	 * @throws IOException
	 */
	private void prepareJsonResponse(SlingHttpServletResponse response) throws JSONException, IOException {
		LOGGER.info("Json Conversion");
		org.json.JSONObject obj = new org.json.JSONObject();
		obj.put("lazyLoadLimit", lazyLoadLimit);

		obj.put("categories", categoryList);

		obj.put("games", tileList);
		if (titleAlign == null) {
			obj.put(aligntitle, "center");
		} else {
			obj.put(aligntitle, titleAlign);
		}
		if (sectionAltTitle == null) {
			obj.put(altTitle, "Play More Games");
		} else {
			obj.put(altTitle, sectionAltTitle);
		}
		obj.put("columnLayout", columnLayout);
		response.setContentType("application/json");
		response.getWriter().print(obj);
		LOGGER.info("EventCreationServlet: doGet end");
	}

	public void setTileGalleryAndLandingService(TileGalleryAndLandingService tileGalleryAndLandingService) {
		this.tileGalleryAndLandingService = tileGalleryAndLandingService;
	}

}
