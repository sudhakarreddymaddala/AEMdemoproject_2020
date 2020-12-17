package com.training.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.wcm.api.Page;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Component(service = Servlet.class, immediate = true, property = {
		Constants.SERVICE_DESCRIPTION + "=Touch UI DropDown Creation Servlet",
		"sling.servlet.paths=" + "/bin/homepagedropdown" })
public class ChildPages extends SlingSafeMethodsServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ChildPages.class);

	/**
	 * The doGet Method to fetch the Child Pages of Home Page
	 */
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		LOGGER.info("doGet of ChilPages---> start");

		try {
			ResourceResolver resolver;
			resolver = request.getResourceResolver();
			String reqPath = request.getRequestURI();
			LOGGER.debug("reqPath value of ChildPages servlet is {}",reqPath);
			String homePagePath = "";
			if (null != reqPath) {
				homePagePath = getHomePagePathFromRequest(reqPath, homePagePath);
				LOGGER.debug("homePagePath value is {}",homePagePath);
			}
			Resource resource = resolver.getResource(homePagePath);
			List<Resource> dropdownList = new ArrayList<>();
			if (resource != null) {
				Page homePage = resource.adaptTo(Page.class);
				if (null != homePage) {
					Iterator<Page> rootPageIterator = homePage.listChildren(null, false);

					while (rootPageIterator.hasNext()) {

						Page currentPage = rootPageIterator.next();
						ValueMap valueMap = currentPage.getProperties();
						ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());
						String title = fetchPageTitle(valueMap);
						LOGGER.debug("homePagePath value of ChildPages servlet is {}",title);
						String value=currentPage.getName();
						if (null != title) {
							vm.put("text", title);
							vm.put("value", value);
							dropdownList.add(new ValueMapResource(request.getResourceResolver(), new ResourceMetadata(),
									"nt:unstructured", vm));
							LOGGER.debug("dropdownList size of ChildPages servlet is {}",dropdownList.size());
						}
					}
				}

				DataSource dataSource = new SimpleDataSource(dropdownList.iterator());
				request.setAttribute(DataSource.class.getName(), dataSource);

			}

		} catch (Exception e) {
			LOGGER.error("Exception occured in doGet method {} ", e.getMessage());
		}
		LOGGER.info("doGet of ChilPages ---> end");
	}

	/**
	 * @param reqPath
	 *            Request Path
	 * @param homePagePath
	 *            Initial home page path
	 * @return homePagePath Actual home page path
	 */
	private String getHomePagePathFromRequest(String reqPath, String homePagePath) {
		LOGGER.info("getHomePagePathFromRequest of ChilPages---> start");
		if (-1 != reqPath.indexOf(com.training.core.constants.Constants.CQ_DIALOG_HTML)) {
			String pathwithdialog = reqPath.substring(
					reqPath.indexOf(com.training.core.constants.Constants.CQ_DIALOG_HTML), reqPath.length());
			LOGGER.debug("pathwithdialog value of ChildPages servlet is {}",pathwithdialog);
			String respath = pathwithdialog.replace(com.training.core.constants.Constants.CQ_DIALOG_HTML, "");
			LOGGER.debug("respath value of ChildPages servlet is {}",respath);
			if (respath.contains("/home/") && respath.startsWith("/content")) {
				homePagePath = respath.substring(0, respath.indexOf("/home/") + 5);
				LOGGER.debug("homePagePath from request is {}",homePagePath);
			}
		}
		LOGGER.info("getHomePagePathFromRequest of ChilPages---> end");
		return homePagePath;
	}

	private String fetchPageTitle(ValueMap valueMap) {
		String title = "";
		if (null != valueMap.get("navTitle", String.class)) {
			title = valueMap.get("navTitle", String.class);
		} else {
			title = valueMap.get("jcr:title", String.class);
		}
		return title;

	}

}