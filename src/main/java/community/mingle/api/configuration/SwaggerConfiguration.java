package community.mingle.api.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class SwaggerConfiguration {

    private final SecurityScheme securityScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER)
            .name("Authorization");
    private final String securityRequirementName = "bearerAuth";

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .servers(Collections.singletonList(new Server().url("/")))
                .security(Collections.singletonList(new SecurityRequirement().addList(securityRequirementName)))
                .components(new Components().addSecuritySchemes(securityRequirementName, securityScheme))
                .info(new Info().title("mingle-api"))
                .externalDocs(new ExternalDocumentation().description("mingle-api"));
    }
}
