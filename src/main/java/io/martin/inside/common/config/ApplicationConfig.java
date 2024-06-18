package io.martin.inside.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.martin.inside.common.databind.XssStringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * @since       2024.06.19
 * @author      martin
 * @description 
 **********************************************************************************************************************/
@Configuration
public class ApplicationConfig {

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

		SimpleModule xssModule = new SimpleModule();
		xssModule.addDeserializer(String.class, new XssStringDeserializer());
		objectMapper.registerModule(xssModule);

		return objectMapper;
	}
}
