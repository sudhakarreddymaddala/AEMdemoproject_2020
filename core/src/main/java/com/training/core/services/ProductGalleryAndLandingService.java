package com.training.core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.training.core.helper.TrainingHelper;
import com.training.core.pojos.ProductAsset;
import com.training.core.pojos.ProductTilePojo;
import com.training.core.utils.TrainingSiteConfigurationUtils;

/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Component(service = ProductGalleryAndLandingService.class, immediate = true)
@Designate(ocd = ProductGalleryAndLandingService.Config.class)
public class ProductGalleryAndLandingService {

	@Reference
	SlingSettingsService slingSettingsService;
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductGalleryAndLandingService.class);

	@Reference
	ResourceResolverFactory resolverFactory;
	@Reference
	QueryBuilder queryBuilder;
	ResourceResolver resolver;
	Session session;

	@Reference
	private MultifieldReader multifieldReader;

	List<ProductTilePojo> masterList = new ArrayList<>();
	String service = "readwriteservice";
	String serviceUser = "playserviceuser";
	String tileThumbnailProp = "tileThumbnail";
	String hoverOverProp = "hoveroverimg";
	String tileAltTextProp = "tileAltTxt";
	String hoverOverAltProp = "hoverOverAlt";
	String alwaysEnglishProp = "alwaysEnglish";

	/**
	 * Method to return list of Products which have authored Manually
	 * 
	 * @param productItems
	 * @param linkToPages
	 * @return
	 */
	public List<ProductTilePojo> getAllTiles(String[] productItems, boolean linkToPages) {

		LOGGER.info("start of getAllTiles method ");

		Map<String, Object> map = new HashMap<>();
		map.put(ResourceResolverFactory.SUBSERVICE, service);
		map.put(ResourceResolverFactory.USER, serviceUser);
		try {
			if (resolverFactory != null) {
				resolver = resolverFactory.getServiceResourceResolver(map);
			}
			if (null != productItems) {
				masterList.clear();
				for (String productItem : productItems) {
					String productDetailPath = productItem + "/jcr:content/productdetail";
					if (null != resolver.getResource(productDetailPath)) {
						Resource productResource = resolver.getResource(productDetailPath);
						if (null != productResource) {
							ProductTilePojo productTilePojo;
							productTilePojo = prepareProductPojo(productResource);
							ValueMap valMap = productResource.getValueMap();
							String productPath = valMap.get("productPath", String.class);
							fetchProductDetailsByProductPath(productPath, productItem, null, "allTiles",
									productTilePojo, linkToPages,resolver);
						}
					}
				}
			}

		} catch (LoginException e) {
			LOGGER.error("Exception caused in get Tiles By Category", e);
		} catch (RepositoryException e) {
			LOGGER.error("Repository Exception caused in get Tiles By Category", e);
		} finally {
			if (null != resolver && resolver.isLive()) {
				resolver.close();
			}
		}
		LOGGER.info("end of getAllTiles method ");
		return masterList;
	}

	/**
	 * Method to prepare Product Pojo
	 * 
	 * @param productResource
	 * @param linkToPages
	 * @return
	 */
	protected ProductTilePojo prepareProductPojo(Resource productResource) {
		ValueMap valMap = productResource.getValueMap();
		String thumbnailHoverImage = valMap.get("productHoverOverImage", String.class);
		String thumbnailHoverImageAltText = valMap.get("productHoverOverImageAltText", String.class);
		String alwaysEnglish = valMap.get(alwaysEnglishProp, String.class);
		ProductTilePojo productTilePojo = new ProductTilePojo();
		productTilePojo.setProductThumbnailHover(thumbnailHoverImage);
		productTilePojo.setThumbnailHoverAltTxt(thumbnailHoverImageAltText);
		productTilePojo.setAlwaysEnglish(alwaysEnglish);
		return productTilePojo;
	}

	/**
	 * Method to Fetch Product Details By Product Path
	 * 
	 * @param productPath
	 * @param productItem
	 * @param hit
	 * @param callFrom
	 * @param productTilePojo
	 * @param linktoPages
	 * @return
	 * @throws RepositoryException
	 */
	protected List<ProductTilePojo> fetchProductDetailsByProductPath(String productPath, String productItem, Hit hit,
			String callFrom, ProductTilePojo productTilePojo, boolean linktoPages,ResourceResolver resResolver) throws RepositoryException {
		if (callFrom.equals("allTiles") && StringUtils.isNotBlank(productPath)) {
			productTilePojo = getProductTileDetails(productPath, resResolver, productItem, false, productTilePojo,
					linktoPages);
			masterList.add(productTilePojo);
		} else if (callFrom.equals("byDate") && StringUtils.isNotBlank(productPath)) {
			String productPageURL = hit.getPath().replace("/jcr:content/productdetail", "");
			productTilePojo = getProductTileDetails(productPath, resResolver, productPageURL, false, productTilePojo,
					linktoPages);
			masterList.add(productTilePojo);
		}
		return masterList;
	}

	@ObjectClassDefinition(name = "Child Page properties")
	public @interface Config {
		@AttributeDefinition(name = "Root path", description = "Please provide the rootpath of retail homepage. Default is /content/play/pollypocket")
		String rootPath() default "/content/play/pollypocket";
	}

	@Activate
	public void activate(final Config config) {
		// Intentionally left Blank
	}

	/**
	 * Method to build a list of Products sorting by Date
	 * 
	 * @param b
	 * 
	 * 
	 **/
	public List<ProductTilePojo> getTilesByDate(String parentPath, boolean linktoPages) {

		LOGGER.info("getTilesByDate method Start");
		Map<String, Object> map = new HashMap<>();
		map.put(ResourceResolverFactory.SUBSERVICE, service);
		map.put(ResourceResolverFactory.USER, serviceUser);

		try {
			if (resolverFactory != null) {
				resolver = resolverFactory.getServiceResourceResolver(map);
			}
			if (resolver != null) {
				session = resolver.adaptTo(Session.class);
			}
			Map<String, String> querrymap = new HashMap<>();
			querrymap.put("path", parentPath);
			querrymap.put("type", "nt:unstructured");
			querrymap.put("property", "sling:resourceType");
			querrymap.put("property.value", "mattel/play/components/content/productDetail");
			querrymap.put("orderby", "@jcr:lastModified");
			querrymap.put("orderby.sort", "desc");
			querrymap.put("p.limit", "-1");
			Query pageQuery = queryBuilder.createQuery(PredicateGroup.create(querrymap), session);
			if (null != pageQuery) {
				SearchResult result = pageQuery.getResult();
				
					masterList.clear();
					if(null!=result){
						for (Hit hit : result.getHits()) {
							if (null != resolver.getResource(hit.getPath())) {
								Resource productResource = resolver.getResource(hit.getPath());
								if (null != productResource) {
									ValueMap valMap = productResource.getValueMap();
									ProductTilePojo productTilePojo;
									productTilePojo = prepareProductPojo(productResource);
									String productPath = valMap.get("productPath", String.class);
									fetchProductDetailsByProductPath(productPath, "", hit, "byDate", productTilePojo,
											linktoPages,resolver);
								}
							}
						}
						TrainingHelper.getLeakingResResolver(result);
					}
				
			}
		} catch (RepositoryException | LoginException e) {
			LOGGER.error("LoginException Exception Occured {} ", e);
		} finally {
			if (null != resolver && resolver.isLive()) {
				resolver.close();
			}
		}
		LOGGER.info("getTilesByDate method End");
		return masterList;
	}

	/**
	 * Method to get Product Tile Related Information
	 * 
	 * @param productPath
	 * @param resolver
	 * @param linktoPages
	 * @return productTilePojo
	 **/
	public ProductTilePojo getProductTileDetails(String productPath, ResourceResolver resolver, String productPageURL,
			boolean isProductDetail, ProductTilePojo productTilePojo, boolean linktoPages) {
		LOGGER.info("getProductTileDetails method Start");
		try {
			Resource productResource = resolver.getResource(productPath);
			PageManager pageManager = resolver.adaptTo(PageManager.class);
			if (pageManager != null) {
				Page productPage = pageManager.getPage(productPageURL);
				if (null != productResource && null != productPage) {
					ValueMap vmap = productResource.getValueMap();
					Tag[] tags = productPage.getTags();
					String productTitle = vmap.get("jcr:title", String.class);
					List<String> pageTags = new ArrayList<>();
					if (null != tags) {
						for (Tag productTag : tags) {
							pageTags.add(productTag.getTagID());
						}
					}
					String productPageTitle = null != productPage.getTitle() ? productPage.getTitle()
							: productPage.getName();
					productTilePojo.setProductPageName(productPageTitle);
					productTilePojo.setProductTitle(productTitle);
					if (linktoPages) {
						productTilePojo.setProductPagePath(TrainingHelper.checkLink(productPageURL,productResource));
					}
					productTilePojo.setProductTags(pageTags);
					productTilePojo.setSelected(false);
					Node productNode = productResource.adaptTo(Node.class);
					checkProductAssetList(productNode, isProductDetail, productTilePojo, resolver);
				}
			}
		} catch (

		RepositoryException e) {
			LOGGER.error("Exception caused while fetching Prodcut Tile Details", e);
		}
		LOGGER.info("getProductTileDetails method End");
		return productTilePojo;
	}

	/**
	 * Method to check Product Asset List
	 * 
	 * @param productNode
	 * @param isProductDetail
	 * @param productTilePojo
	 * @param resolver
	 * @throws RepositoryException
	 */
	private void checkProductAssetList(Node productNode, boolean isProductDetail, ProductTilePojo productTilePojo,
			ResourceResolver resolver) throws RepositoryException {

		List<ProductAsset> productAssetList = new ArrayList<>();
		if (null != productNode) {
			productTilePojo.setage(checkProperty(productNode, "ageGrade"));
			productTilePojo.setDescription(checkProperty(productNode, "jcr:description"));
			productTilePojo.setProductId(checkProperty(productNode, "identifier"));
			if (productNode.hasNode("assets")) {
				Node assetsNode = productNode.getNode("assets");
				if (null != assetsNode) {
					Map<String, ValueMap> multifieldProperty;
					multifieldProperty = multifieldReader.propertyReader(assetsNode);
					for (Map.Entry<String, ValueMap> entry : multifieldProperty.entrySet()) {
						String tileImage = entry.getValue().get("fileReference", String.class);
						String tileImageAltText = TrainingHelper.getAssetMetadataValue(tileImage, resolver,
								"dc:imageAltText");
						fetchProductAssetData(isProductDetail, productTilePojo, tileImage, tileImageAltText,
								productAssetList, resolver);
					}
					productTilePojo.setProductImages(productAssetList);
				}
			}
		}

	}

	/**
	 * Method to fetch Product Assets Data
	 * 
	 * @param isProductDetail
	 * @param productTilePojo
	 * @param tileImage
	 * @param tileImageAltText
	 * @param productAssetList
	 * @param resolver
	 */
	private void fetchProductAssetData(boolean isProductDetail, ProductTilePojo productTilePojo, String tileImage,
			String tileImageAltText, List<ProductAsset> productAssetList, ResourceResolver resolver) {
		ProductAsset productAsset;
		if (!isProductDetail) {
			checkAssetIsPrimary(resolver, productTilePojo, tileImage, tileImageAltText);
		} else {
			productAsset = new ProductAsset();
			checkAssetisVideo(resolver, productAsset, tileImage);
			productAsset.setProductImage(tileImage);
			productAsset.setProductAltText(tileImageAltText);
			productAssetList.add(productAsset);
		}

	}

	/**
	 * Method to check Whether the Asset is primary or not
	 * 
	 * @param resolver
	 * @param productTilePojo
	 * @param tileImage
	 * @param tileImageAltText
	 */
	private void checkAssetIsPrimary(ResourceResolver resolver, ProductTilePojo productTilePojo, String tileImage,
			String tileImageAltText) {
		LOGGER.info("Start of checkAssetIsPrimary method ");
		if (StringUtils.isNotBlank(tileImage)) {
			Resource imageRes = resolver.getResource(tileImage);
			if (null != imageRes) {
				Asset imageAsset = imageRes.adaptTo(Asset.class);
				if (null != imageAsset) {
					Object[] tags = (Object[]) imageAsset.getMetadata("cq:tags");
					TagManager tagmanager = resolver.adaptTo(TagManager.class);
					if (null != tags && null != tagmanager) {
						fetchAssetIsPrimary(tags, tagmanager, productTilePojo, tileImage, tileImageAltText);
					}
				}

			}
		}
		LOGGER.info("End of checkAssetIsPrimary method ");
	}

	/**
	 * Method to fetch the Primary Asset
	 * 
	 * @param tags
	 * @param tagmanager
	 * @param productTilePojo
	 * @param tileImage
	 * @param tileImageAltText
	 */
	private void fetchAssetIsPrimary(Object[] tags, TagManager tagmanager, ProductTilePojo productTilePojo,
			String tileImage, String tileImageAltText) {
		LOGGER.info("Start fetchAssetIsPrimary method ");
		for (Object obj : tags) {
			Tag tag = tagmanager.resolve(obj.toString());
			if (null != tag) {
				String tagName = tag.getName();
				LOGGER.debug("Product Tag Name{}", tagName);
				if (TrainingSiteConfigurationUtils.getProductPrimaryImageTag().equalsIgnoreCase(tagName)) {
					productTilePojo.setProductThumbnail(tileImage);
					productTilePojo.setThumbnailAltTxt(tileImageAltText);
				}
			}
		}
		LOGGER.info("End fetchAssetIsPrimary method ");
	}

	/**
	 * Method to check if Asset is Video
	 * 
	 * @param resolver
	 * @param productAsset
	 * @param assetPath
	 * @return
	 */
	private ProductAsset checkAssetisVideo(ResourceResolver resolver, ProductAsset productAsset, String assetPath) {

		Resource resource = resolver.getResource(assetPath);
		if (resource != null) {
			Asset videoAsset = resource.adaptTo(Asset.class);
			if (videoAsset != null && videoAsset.getMetadata("dc:ooyalaID") != null) {
				productAsset.setVideo(true);
				productAsset.setOoyalaId(videoAsset.getMetadataValue("dc:ooyalaID"));
			}
		}
		return productAsset;
	}

	/**
	 * Method to check for the property Value
	 * 
	 * @param productNode
	 * @param string
	 * @return
	 */
	private String checkProperty(Node productNode, String string) {
		try {
			return productNode.hasProperty(string) ? productNode.getProperty(string).getValue().getString() : "";
		} catch (Exception e) {
			LOGGER.error("Exception caused while fetching properties from productnode", e);
		}
		return "";
	}

	/**
	 * Method to filter Products By Tag
	 * 
	 * @param allProducts
	 * @param galleryTagId
	 * @return
	 */
	public List<ProductTilePojo> filterProductsByTag(List<ProductTilePojo> allProducts, String galleryTagId) {
		List<ProductTilePojo> categoryProductsList = new ArrayList<>();
		for (ProductTilePojo productTilePojo : allProducts) {
			boolean tagMatch = false;
			List<String> productTags = productTilePojo.getProductTags();
			if (null != productTags) {
				if (productTags.contains(galleryTagId)) {
					tagMatch = true;
				}
				if (tagMatch) {
					categoryProductsList.add(productTilePojo);
				}
			}
		}
		return categoryProductsList;
	}

	public void setSlingSettingsService(SlingSettingsService slingSettingsService) {
		this.slingSettingsService = slingSettingsService;
	}

	public void setResolverFactory(ResourceResolverFactory resolverFactory) {
		this.resolverFactory = resolverFactory;
	}

	public void setQueryBuilder(QueryBuilder queryBuilder) {
		this.queryBuilder = queryBuilder;
	}

	public void setResolver(ResourceResolver resolver) {
		this.resolver = resolver;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public void setTileAltTextProp(String tileAltTextProp) {
		this.tileAltTextProp = tileAltTextProp;
	}

	public void setMultifieldReader(MultifieldReader multifieldReader) {
		this.multifieldReader = multifieldReader;
	}

}
