package com.training.core.services;

import org.apache.sling.api.resource.*;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Component(service = MultifieldReader.class, immediate = true)
@Designate(ocd = MultifieldReader.Config.class)
public class MultifieldReader {

	private static final Logger LOGGER = LoggerFactory.getLogger(MultifieldReader.class);

	@Reference
	ResourceResolverFactory resourceResolverFactory;

	/**
	 * This returns the map.
	 *
	 * @param childNodes parameter
	 * @return map
	 */
	public Map<String, ValueMap> propertyReader(Node childNodes) {
		LOGGER.info("start of propertyReader");
		Map<String, Object> map = new HashMap<>();
		NodeIterator nodeIterator = null;
		map.put(ResourceResolverFactory.SUBSERVICE, "readwriteservice");
		map.put(ResourceResolverFactory.USER, "trainingserviceuser");
		ResourceResolver resolver = null;
		LinkedHashMap<String, ValueMap> propertiesMap = new LinkedHashMap<>();

		try {
			if (null != resourceResolverFactory) {
				resolver = resourceResolverFactory.getServiceResourceResolver(map);
			}
			if (null != childNodes) {
				nodeIterator = childNodes.getNodes();
			}
			if (null != nodeIterator && null != resolver) {
				while (nodeIterator.hasNext()) {
					Node itemNode = nodeIterator.nextNode();
					LOGGER.debug("Node Names are {}", itemNode.getName());
					Resource resource = resolver.getResource(itemNode.getPath());
					if (resource != null) {
						ValueMap valueMap = new ValueMapDecorator(new HashMap<>());
						valueMap.putAll(resource.getValueMap());
						propertiesMap.put(itemNode.getName(), valueMap);
					}
				}
			}
		} catch (LoginException | RepositoryException e) {
			LOGGER.error("Exception Caused in Multifield Reader Service {}", e);
		} finally {
			LOGGER.info("start of finally in propertyReader() Method");
			if (resolver != null && resolver.isLive()) {
				resolver.close();
			}
			LOGGER.info("End of finally in propertyReader() Method");

		}

		LOGGER.info("End of propertyReader");
		return propertiesMap;
	}

	@ObjectClassDefinition(name = "Multifield reader service")
	public @interface Config {

	}

	public void setResourceResolverFactory(ResourceResolverFactory resourceResolverFactory) {
		this.resourceResolverFactory = resourceResolverFactory;
	}

}
