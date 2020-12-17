
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
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONException;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.training.core.pojos.InterstitialPojo;
import com.training.core.utils.PropertyReaderUtils;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Component(service = Servlet.class, immediate = true, property = {
		Constants.SERVICE_DESCRIPTION + "=Download Interstitial App Json Creation Servlet",
		"sling.servlet.paths=" + "/bin/getDownloadInterApp" })
public class DownloadInterstitialAppServlet extends SlingSafeMethodsServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadInterstitialAppServlet.class);

	/**
	 * The doGet Method to fetch the Download Interstitial Details
	 */
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		LOGGER.info("doGet of DownloadInterstitialAppServlet---> start");
		ResourceResolver resolver;
		Resource resource;
		String path = request.getParameter("downloadPath");
		LOGGER.debug("path value of DownloadInterstitialAppServlet is {}",path);
		path = path.trim() + PropertyReaderUtils.getDownloadInterstitialApp();
		String downloadId = request.getParameter("downloadId");
		LOGGER.debug("downloadId value of DownloadInterstitialAppServlet is {}",downloadId);
		String[] split = downloadId.split("_");
		String val = split[1];
		int id = split[1] != null ? Integer.parseInt(val) : 0;
		List<InterstitialPojo> list = new ArrayList<>();
		try {

			resolver = request.getResourceResolver();
			resource = resolver.getResource(path);
			if (resource != null) {
				Node node = resource.adaptTo(Node.class);
				if (node != null) {
					list = fetchInterstitialDetails(node, id);
					LOGGER.debug("list size of DownloadInterstitialAppServlet is {}",list.size());
					org.json.JSONObject obj = new org.json.JSONObject();
					obj.put("downloadList", list);
					response.setContentType("application/json");
					response.getWriter().print(obj);
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
		LOGGER.info("doGet of DownloadInterstitialAppServlet---> end");
	}

	/**
	 * Method to fetch the Interstitial Details from the Node
	 * 
	 * @param node
	 * @param id
	 * @return
	 * @throws RepositoryException
	 */
	private List<InterstitialPojo> fetchInterstitialDetails(Node node, int id) throws RepositoryException {
		LOGGER.debug("fetchInterstitialDetails of DownloadInterstitialAppServlet ---> start");
		List<InterstitialPojo> list = new ArrayList<>();
		NodeIterator itr = node.getNodes();
		while (itr.hasNext()) {
			if (itr.getPosition() == id) {
				Node itr1Node = itr.nextNode();
				NodeIterator itr2Node = itr1Node.getNodes();
				while (itr2Node.hasNext()) {
					Node itr3Node = itr2Node.nextNode();

					NodeIterator lastNode = itr3Node.getNodes();
					while (lastNode.hasNext()) {
						Node fNode = lastNode.nextNode();

						InterstitialPojo downloadinterstitialpojo = new InterstitialPojo();
						downloadinterstitialpojo
								.setInterstitialLogoSrc(fNode.getProperty("interstitialLogo").getString());
						downloadinterstitialpojo.setInterstitialUrl(fNode.getProperty("interstitialUrl").getString());
						downloadinterstitialpojo
								.setInterstitialLogoAlt(fNode.getProperty("interstitialLogoAlt").getString());
						downloadinterstitialpojo
								.setInterstitialTarget(fNode.getProperty("interstitialTarget").getString());
						list.add(downloadinterstitialpojo);

					}

				}
			} else {
				itr.next();
			}

		}
		LOGGER.debug("fetchInterstitialDetails of DownloadInterstitialAppServlet---> end");
		return list;
	}

}
