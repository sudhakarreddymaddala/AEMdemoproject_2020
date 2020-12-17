package com.training.core.utils;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
/**
 * 
 * @author Sudhakar Reddy Maddala
 *
 */
@Component(service = CategoryFilterSlidesUtils.class, immediate = true)
@Designate(ocd = CategoryFilterSlidesUtils.Config.class)
public class CategoryFilterSlidesUtils {
	
	private static String[] slideShowValueMapping;
	private static String[] characterSlidesValueMapping;

	@Activate
	public void activate(final Config config) {
		
		slideShowValueMapping = config.slideShowValueMapping();
		characterSlidesValueMapping = config.characterSlidesValueMapping();

	}

	@ObjectClassDefinition(name = "Play Category Filter Slides Utils")
	public @interface Config {
		
		@AttributeDefinition(name = "Category Filter Slides Mapping", description = "Add Category Filter Slides Mapping as brandname:slidecount")
		String[] slideShowValueMapping();
		
		@AttributeDefinition(name = "Character Detail Carousel Slides Mapping", description = "Add Character Detail Carousel Slides Mapping as brandname:slidecount")
		String[] characterSlidesValueMapping();

	}

	public static String[] getSlideShowValueMapping() {
		return slideShowValueMapping;
	}
	
	public static String[] getCharacterSlidesValueMapping() {
		return characterSlidesValueMapping;
	}

}
