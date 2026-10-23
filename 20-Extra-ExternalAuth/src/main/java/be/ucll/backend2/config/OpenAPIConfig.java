package be.ucll.backend2.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Movies API", version = "1.0")
)
@SecurityScheme(
        name = "authentik",
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(
            authorizationCode = @OAuthFlow(
                authorizationUrl = "http://localhost:9000/application/o/authorize/",
                tokenUrl = "http://localhost:9000/application/o/token/",
                scopes = {
                    @OAuthScope(
                        name = "openid",
                        description = "OpenID Connect"
                    ),
                    @OAuthScope(
                        name = "profile"
                    ),
                    @OAuthScope(
                        name = "email"
                    )
                }
            )
        )
)
public class OpenAPIConfig {
}
