package com.example.login.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI(){

		return new OpenAPI()
			.info(new Info()
				.title("DODO's Login API")
				.version("1.0")
				.description("API 명세서"));
	}


}
