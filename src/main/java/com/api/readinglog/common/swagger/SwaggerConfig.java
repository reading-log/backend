package com.api.readinglog.common.swagger;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Arrays;
import java.util.Collections;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        servers = {@Server(url = "/")},
        info = @Info(
                title = "리딩로그 API 명세서",
                description = "독서 기록 서비스 리딩로그의 API 명세서",
                contact = @Contact(name = "the developer", email = "ddmkim94@gmail.com"),
                version = "v1.0")
)
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(){
        String securityRequirementName = "Bearer를 제외한 accessToken값을 넣어주세요.";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securityRequirementName);

        Components components = new Components()
                .addSecuritySchemes(securityRequirementName, new SecurityScheme()
                        .name(securityRequirementName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .components(components)
                .addSecurityItem(securityRequirement);
    }
}
