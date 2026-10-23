package be.ucll.backend2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("disable-security")
public class BypassSecurityConfig {

    @Bean
    public SecurityFilterChain bypassSecurityFilterChain(HttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(
                authorizeRequests ->
                    authorizeRequests.anyRequest().permitAll()
            )
            .build();
    }

}
