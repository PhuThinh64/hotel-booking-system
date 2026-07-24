package com.example.hotel_booking.config.swagger;

import com.example.hotel_booking.config.swagger.constants.ApiInfoConstants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.ExternalDocumentation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI hotelBookingOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(apiServers())
                .components(apiComponents())
                .externalDocs(
                        new ExternalDocumentation()
                                .description("Project Repository")
                                .url(ApiInfoConstants.PROJECT_REPOSITORY)
                )
                .addSecurityItem(new SecurityRequirement().addList(ApiInfoConstants.SECURITY_SCHEME));
    }

    private Info apiInfo() {
        return new Info()
                .title(ApiInfoConstants.TITLE)
                .version(ApiInfoConstants.VERSION)
                .description(ApiInfoConstants.API_DESCRIPTION)
                .contact(apiContact())
                .license(apiLicense());
    }

    private Contact apiContact() {
        return new Contact()
                .name(ApiInfoConstants.CONTACT_NAME)
                .email(ApiInfoConstants.CONTACT_EMAIL);

    }

    private License apiLicense() {
        return new License()
                .name(ApiInfoConstants.LICENSE_NAME)
                .url(ApiInfoConstants.LICENSE_URL);
    }

    private List<Server> apiServers() {
        return List.of(
                new Server().url("/")
        );
    }

    private Components apiComponents() {
        return new Components()
                .addSecuritySchemes(
                        ApiInfoConstants.SECURITY_SCHEME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme(ApiInfoConstants.JWT_SCHEME)
                                .bearerFormat(ApiInfoConstants.JWT_BEARER_FORMAT)
                                .description(ApiInfoConstants.JWT_DESCRIPTION)
                );
    }
}