package com.training.core.servlets;

import java.io.IOException;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONException;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.training.core.pojos.VideoCategory;
import com.training.core.pojos.VideoTile;
import com.training.core.services.VideoGalleryService;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Component(service = Servlet.class, immediate = true, property = {
		Constants.SERVICE_DESCRIPTION + "=Video Landing Json Creation Servlet",
		"sling.servlet.paths=" + "/bin/getVideoLandingGrid" })
public class VideoLandingServlet extends SlingSafeMethodsServlet {
	private static final long serialVersionUID = 5188488628730119031L;
	@Reference
	private transient VideoGalleryService videoGalleryService;
	private static final Logger LOGGER = LoggerFactory.getLogger(VideoLandingServlet.class);
	private transient List<VideoTile> videoList;
	private transient List<VideoCategory> categoryList;
	String orderVideo;
	String allCatLabel;
	String catDisplay;
	String colLayout;
	String autoPlay;
	String nameAlign;
	String onLoadLimit;
	String relVideosTitle;
	String landingNode;
	String manual = "manual";
	String automatic = "automatic";
	String ooyalaPlayerId;
	String playerId = "ooyalaPlayerId";
	String sectionAltTitle;
	String secAltTitle = "sectionAltTitle";
    String videoAssetPath;
	String allCatAnalyticsLabel;

	/**
	 * The doGet method to fetch the List of Videos and Video Landing Grid
	 * Properties
	 */
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		LOGGER.info("doGet method of VideoLandingServlet started");
		String currentNodePath = request.getParameter("currentPath");
		LOGGER.debug("currentNodePath value of VideoLandingServlet is {}",currentNodePath);
		ResourceResolver resolver = request.getResourceResolver();
		Resource currentResource = resolver.getResource(currentNodePath);
		try {
			if (currentResource != null) {
				Node videoNode = currentResource.adaptTo(Node.class);
				if (videoNode != null) {
					getLandingProperties(videoNode);
					if (orderVideo != null) {
						videoList = videoGalleryService.getVideoList(orderVideo, currentNodePath,videoAssetPath);
						LOGGER.debug("videoList size of VideoLandingServlet is {}", videoList.size());
						setLayoutValues(videoList);

					}
					categoryList = videoGalleryService.getVideoCategoryList(videoNode, allCatLabel, allCatAnalyticsLabel, resolver);
					LOGGER.debug("videoList size of VideoLandingServlet is {}",videoList.size());
					prepareJson(response);
				}
			}
		} catch (NullPointerException e) {
			LOGGER.error("Null PointerException Occured {} ", e);
		} catch (RepositoryException e) {
			LOGGER.error("RepositoryException Exception Occured {} ", e);
		} catch (JSONException e) {
			LOGGER.error("JSON Exception Occured {} ", e);
		} catch (IOException e) {
			LOGGER.error("IOException Occured {} ", e);
		} catch (LoginException e) {
			LOGGER.error("LoginException Occured {} ", e);
		}
		LOGGER.info("doGet method of VideoLandingServlet end");

	}

	/**
	 * Method to fetch the Video Landing Grid Properies
	 * 
	 * @param videoNode
	 * @throws RepositoryException
	 */
	private void getLandingProperties(Node videoNode) throws RepositoryException {
		orderVideo = videoNode.hasProperty("orderVideo") ? videoNode.getProperty("orderVideo").getString() : "";
		ooyalaPlayerId = videoNode.hasProperty(playerId) ? videoNode.getProperty(playerId).getString() : "";
		allCatLabel = videoNode.hasProperty("allLabel") ? videoNode.getProperty("allLabel").getString() : "";
		catDisplay = videoNode.hasProperty("categoryDisplay") ? videoNode.getProperty("categoryDisplay").getString()
				: "";
		colLayout = videoNode.hasProperty("columnLayout") ? videoNode.getProperty("columnLayout").getString() : "";
		autoPlay = videoNode.hasProperty("disableAutoplay") ? videoNode.getProperty("disableAutoplay").getString() : "";
		nameAlign = videoNode.hasProperty("nameAlign") ? videoNode.getProperty("nameAlign").getString() : "";
		onLoadLimit = videoNode.hasProperty("onLoadLimit") ? videoNode.getProperty("onLoadLimit").getString() : "";
		relVideosTitle = videoNode.hasProperty("relVideoTitle") ? videoNode.getProperty("relVideoTitle").getString()
				: "";
		sectionAltTitle = videoNode.hasProperty(secAltTitle) ? videoNode.getProperty(secAltTitle).getString() : "";
        videoAssetPath = videoNode.hasProperty("videoAssetPath") ? videoNode.getProperty("videoAssetPath").getString()
				: "";
		allCatAnalyticsLabel = videoNode.hasProperty("allAnalyticsName") ? videoNode.getProperty("allAnalyticsName").getString() : "";
	}

	/**
	 * Method to set the Layout Values to the VideoTile Pojo
	 * 
	 * @param videoList
	 */
	private void setLayoutValues(List<VideoTile> videoList) {
		if (videoList != null) {
			for (VideoTile videoTile : videoList) {
				videoTile.setColLayout(colLayout);
				nameAlign = nameAlign.isEmpty() ? "center" : nameAlign;
				videoTile.setTitleAlign(nameAlign);
			}
		}
	}

	/**
	 * Method to Prepare the JSON Response
	 * 
	 * @param response
	 * @throws JSONException
	 * @throws IOException
	 */
	private void prepareJson(SlingHttpServletResponse response) throws JSONException, IOException {
		if (videoList != null) {
			LOGGER.debug("Json Conversion");
			org.json.JSONObject obj = new org.json.JSONObject();
			obj.put("lazyLoadLimit", onLoadLimit);
			obj.put("categories", categoryList);
			obj.put("videos", videoList);
			obj.put(playerId, ooyalaPlayerId);
			obj.put(secAltTitle, sectionAltTitle);
			if (relVideosTitle.isEmpty()) {
				obj.put("relVideosTitle", "Up Next");
			} else {
				obj.put("relVideosTitle", relVideosTitle);
			}
			obj.put("autoPlay", autoPlay);
			response.setContentType("application/json");
			response.getWriter().print(obj);
			LOGGER.debug("EventCreationServlet: doGet end");
		}
	}

	public void setVideoGalleryService(VideoGalleryService videoGalleryService) {
		this.videoGalleryService = videoGalleryService;
	}

}
