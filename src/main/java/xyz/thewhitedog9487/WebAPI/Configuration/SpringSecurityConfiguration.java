package xyz.thewhitedog9487.WebAPI.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class SpringSecurityConfiguration {

    @Bean
    SecurityFilterChain CustomSecurityFilterChain(HttpSecurity Security) throws Exception {
        Security
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(AuthorizationManagerRequestMatcherRegistry -> {
                    AuthorizationManagerRequestMatcherRegistry
                            .requestMatchers("/ip/**", "/message/**")
                            .permitAll()
                            .requestMatchers("/", "/v3/api-docs/**","swagger-ui/**",  "/swagger-ui.html")
                            .permitAll()
                            .anyRequest()
                            .denyAll(); });
        return Security.build(); }
}
