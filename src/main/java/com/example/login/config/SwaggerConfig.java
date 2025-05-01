package com.example.login.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
@OpenAPIDefinition(
	info = @Info(
		title = "DODO's Login API" ,
		version = "1.0",
		description = "API 명세서"
	)
)
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {

		return new OpenAPI()
			.components(new Components().addSecuritySchemes(
				"bearer-auth",
				new SecurityScheme()
					.type(SecurityScheme.Type.HTTP)
					.scheme("bearer")
					.bearerFormat("JWT")
			))
			.addSecurityItem(
				new SecurityRequirement().addList("bearer-auth")
			);
	}
}
