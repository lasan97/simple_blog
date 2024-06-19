package io.martin.inside.common.config;

import io.martin.inside.common.databind.XssStringDeserializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @since       2024.06.19
 * @author      martin
 * @description 
 **********************************************************************************************************************/
@Configuration
public class ApplicationConfig {

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer customJsonDeserializer() {
		return builder -> builder.deserializerByType(String.class, new XssStringDeserializer());
	}
}
