package dev.geoearth.admin.config.openapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "${app.openapi.title}",
                version = "${app.openapi.version}",
                description = "${app.openapi.description}"
        )
)
public class OpenApiConfig {
}