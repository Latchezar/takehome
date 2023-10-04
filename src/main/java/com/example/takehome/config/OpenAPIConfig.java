package com.example.takehome.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Configuration
public class OpenAPIConfig {
    static {
        var localDateSchema = new Schema<LocalDate>();
        localDateSchema.example(LocalDate.now().format(JacksonConfig.DATE_FORMATTER)).type("string");
        SpringDocUtils.getConfig().replaceWithSchema(LocalDate.class, localDateSchema);

        var localDateTimeSchema = new Schema<LocalDateTime>();
        localDateTimeSchema.example(LocalDateTime.now().format(JacksonConfig.DATE_TIME_FORMATTER)).type("string");
        SpringDocUtils.getConfig().replaceWithSchema(LocalDateTime.class, localDateTimeSchema);
    }

    @Bean
    public OpenAPI usersMicroserviceOpenAPI() {
        final String securitySchemeName = "JWT Bearer";
        return new OpenAPI()
                .info(new Info().title("TakeHome Task API")
                                .description(
                                        "The API for the TakeHome Task. In order to use the API you need to create a token from the `/auth/token` endpoint. After that put it in "
                                        + "the `Authorize` section and call the other endpoints.`")
                                .version("1.0"))
                .addSecurityItem(new SecurityRequirement()
                                         .addList(securitySchemeName))
                .components(new Components()
                                    .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                            .name(securitySchemeName)
                                            .type(SecurityScheme.Type.HTTP)
                                            .scheme("bearer")
                                            .bearerFormat("JWT")));

    }
}
