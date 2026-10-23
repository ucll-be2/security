package be.ucll.backend2.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.h2console.autoconfigure.H2ConsoleProperties;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(H2ConsoleProperties.class)
@Profile("!disable-security")
public class SecurityConfig {
    // Source: https://docs.spring.io/spring-boot/reference/data/sql.html#data.sql.h2-web-console.spring-security
    @Bean
    @Order(0)
    @ConditionalOnBooleanProperty(prefix = "spring.h2.console", name = "enabled")
    public SecurityFilterChain h2SecurityFilterChain(HttpSecurity http) {
        return http
                .securityMatcher(PathRequest.toH2Console())
                .csrf(csrf -> csrf.disable())
                .headers(headers ->
                    headers.frameOptions(frameOptionsConfig -> frameOptionsConfig.sameOrigin()))
                .build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationConverter jwtAuthenticationConverter) {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(
                        sessionManagement ->
                                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(
                    oauth2 -> oauth2.jwt(
                        jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .authorizeHttpRequests(
                        authorizeRequests ->
                                authorizeRequests
                                        // Laat OpenAPI toegang toe
                                        .requestMatchers("/v3/api-docs/**").permitAll()
                                        // Laat toegang toe tot Swagger UI
                                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
                                        .requestMatchers("/error").permitAll()
                                        .anyRequest().authenticated()
                )
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        final var converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            final var groups = Optional.ofNullable(jwt.getClaimAsStringList("groups")).orElse(List.of());

            return groups.stream()
                .map(g -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + g.toUpperCase(Locale.ROOT)))
                .toList();
        });

        return converter;
    }
}
