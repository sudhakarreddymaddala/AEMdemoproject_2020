package com.training.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
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
import com.training.core.pojos.TilePojo;
import com.training.core.services.TileGalleryAndLandingService;
import com.training.core.utils.PropertyReaderUtils;

/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Component(service = Servlet.class, immediate = true, property = {
		Constants.SERVICE_DESCRIPTION + "=Character Landing Json Creation Servlet",
		"sling.servlet.paths=" + "/bin/getCharacterLandingGrid" })
public class CharacterLandingServlet extends SlingSafeMethodsServlet {
	private static final long serialVersionUID = 7356232065994015610L;
	private static final Logger LOGGER = LoggerFactory.getLogger(CharacterLandingServlet.class);
	private transient List<TilePojo> characterList = new ArrayList<>();
	private transient List<TilePojo> tileList = new ArrayList<>();

	@Reference
	private transient TileGalleryAndLandingService tileGalleryAndLandingService;
	String orderCharacter;
	String lazyLoadLimit;
	String tileType = "characters";
	String tilePage = "landing";
	String columnLayout;
	String landingPageNode;
	boolean charactersToLink = true;

	/**
	 * The doGet method to fetch the Character Landing Servlet Properties and List
	 * of Characters
	 */
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		LOGGER.info("doGet of CharacterLandingServlet -> Start");
		String currentPath = request.getParameter("currentPath");
		LOGGER.debug("currentPath value of CharacterLandingServlet is {}",currentPath);
		try {
			ResourceResolver resolver = request.getResourceResolver();
			Resource pageResource = resolver.getResource(currentPath);
			if (pageResource != null) {
				PageManager pageManager = pageResource.getResourceResolver().adaptTo(PageManager.class);
				if (pageManager != null) {
					Page page = pageManager.getContainingPage(pageResource);
					Tag[] categoryTags = page.getTags();
					int tagLength = categoryTags.length;
					currentPath= fetchLandingNodePath(tagLength, page, currentPath);
					LOGGER.debug("Updated currentPath value of CharacterLandingServlet is {}",currentPath);
					if (landingPageNode != null) {
						fetchNodeProperties(resolver);
						getCharacterList(resolver, currentPath);
						filterCategory(categoryTags, tagLength);
						prepareJson(response, tagLength);
					}
				}
			}

		} catch (NullPointerException e) {
			LOGGER.error("Null PointerException Occured {} ", e);
		} catch (JSONException e) {
			LOGGER.error("JSON Exception Occured {} ", e);
		} catch (RepositoryException e) {
			LOGGER.error("RepositoryException Exception Occured {} ", e);
		} catch (IOException e) {
			LOGGER.error("IOException Occured {} ", e);
		}
		LOGGER.info("doGet of CharacterLandingServlet-> End");
	}

	/**
	 * Method to prepare the JSON Response
	 * 
	 * @param response
	 * @param tagLength
	 * @throws JSONException
	 * @throws IOException
	 */
	private void prepareJson(SlingHttpServletResponse response, int tagLength) throws JSONException, IOException {
		LOGGER.info("prepareJson of CharacterLandingServlet---> start");
		org.json.JSONObject obj = new org.json.JSONObject();
		obj.put("lazyLoadLimit", lazyLoadLimit);
		if (tagLength != 0) {
			obj.put(tileType, tileList);
		} else {
			obj.put(tileType, characterList);
		}
		obj.put("columnLayout", columnLayout);
		response.setContentType("application/json");
		response.getWriter().print(obj);
		LOGGER.info("prepareJson of CharacterLandingServlet---> end");
	}

	/**
	 * Method to fetch the current Page Category tag details
	 * 
	 * @param categoryTags
	 * @param tagLength
	 */
	private void filterCategory(Tag[] categoryTags, int tagLength) {
		LOGGER.info("filterCategory of CharacterLandingServlet---> start");
		if (tagLength != 0) {
			for (TilePojo tilePojo : characterList) {
				boolean tagMatch = false;
				List<String> tagList = tilePojo.getTileTags();
				for (Tag catTagId : categoryTags) {
					if (tagList.contains(catTagId.getTagID()))
						tagMatch = true;
				}
				if (tagMatch) {
					tileList.add(tilePojo);
				}

			}
		}
		LOGGER.info("filterCategory of CharacterLandingServlet---> end");
	}

	/**
	 * Method to Fetch the list of Characters
	 * 
	 * @param homePage
	 * @param resolver
	 * @param currentPath
	 */
	private void getCharacterList(ResourceResolver resolver, String currentPath) {
		LOGGER.info("getCharacterList of CharacterLandingServlet ---> start");
		if (orderCharacter != null) {
			tileList.clear();
			if (orderCharacter.equals("manual")) {
				characterList = tileGalleryAndLandingService.getAllTiles(landingPageNode, tileType, tilePage,
						charactersToLink);
			} else if (orderCharacter.equals("automatic")) {
				characterList = tileGalleryAndLandingService.getTilesByDate(currentPath, tileType, null, resolver,
						charactersToLink);
				LOGGER.debug("characterList size of CharacterLandingServlet is {}",characterList.size());
			}
		}
		LOGGER.info("getCharacterList of CharacterLandingServlet ---> end");
	}

	/**
	 * Method to fetch the Landing Node Path for Default Category
	 * 
	 * @param tagLength
	 * @param page
	 * @param currentPath
	 */
	private String fetchLandingNodePath(int tagLength, Page page, String currentPath) {
		LOGGER.info("fetchLandingNodePath of CharacterLandingServlet ---> start");
		if (tagLength != 0) {
			Page parentPage = page.getParent();
			if (null != parentPage && StringUtils.isNoneEmpty(parentPage.getPath())) {
				landingPageNode = parentPage.getPath() + PropertyReaderUtils.getCharacterLandingPath();
				currentPath = parentPage.getPath();
			}
		} else {
			landingPageNode = currentPath + PropertyReaderUtils.getCharacterLandingPath();
			LOGGER.debug("landingPageNode value of CharacterLandingServlet is {}",landingPageNode);
		}
		LOGGER.info("fetchLandingNodePath of CharacterLandingServlet ---> end");
		return currentPath;

	}

	/***
	 * Method to fetch the Node properties
	 * 
	 * @param resolver
	 * @throws RepositoryException
	 */
	private void fetchNodeProperties(ResourceResolver resolver) throws RepositoryException {
		LOGGER.info("fetchNodeProperties of CharacterLandingServlet ---> start");
		Resource landingNodeResource = resolver.getResource(landingPageNode);
		if (landingNodeResource != null) {
			Node landingNode = landingNodeResource.adaptTo(Node.class);
			if (landingNode != null) {
				orderCharacter = landingNode.getProperty("orderCharacter").getString();
				LOGGER.debug("orderCharacter value of CharacterLandingServlet is {}",orderCharacter);
				lazyLoadLimit = landingNode.hasProperty("displayCharacters")
						? landingNode.getProperty("displayCharacters").getString()
						: "3";
				LOGGER.debug("lazyLoadLimit value of CharacterLandingServlet is {}",lazyLoadLimit);
				columnLayout = landingNode.getProperty("columnLayout").getString();
				if (landingNode.hasProperty("charactersToLink")) {
					charactersToLink = landingNode.getProperty("charactersToLink").getBoolean();
				}
			}
		}
		LOGGER.info("fetchNodeProperties of CharacterLandingServlet ---> end");
	}

	public void setLandingPageNode(String landingPageNode) {
		this.landingPageNode = landingPageNode;
	}

	public void setCharacterList(List<TilePojo> characterList) {
		this.characterList = characterList;
	}

	public void setTileGalleryAndLandingService(TileGalleryAndLandingService tileGalleryAndLandingService) {
		this.tileGalleryAndLandingService = tileGalleryAndLandingService;
	}

}