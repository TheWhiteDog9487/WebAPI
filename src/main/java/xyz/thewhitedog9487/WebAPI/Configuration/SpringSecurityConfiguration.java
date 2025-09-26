package xyz.thewhitedog9487.WebAPI.Configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import xyz.thewhitedog9487.WebAPI.Configuration.Security.ApiKeyAuthenticationFilter;

@Configuration
class SpringSecurityConfiguration {
    @Autowired ApiKeyAuthenticationFilter ApiKeyAuthenticationFilter;

    /**
     * @see ApiKeyAuthenticationFilter#shouldNotFilter(HttpServletRequest) 
     */
    @Bean
    SecurityFilterChain CustomSecurityFilterChain(HttpSecurity Security) throws Exception {
        Security
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(AuthorizationManagerRequestMatcherRegistry -> {
                    AuthorizationManagerRequestMatcherRegistry
                            .requestMatchers("/ip/**")
                            .permitAll()
                            .requestMatchers("/", "/v3/api-docs/**","swagger-ui/**",  "/swagger-ui.html")
                            .permitAll()
                            .requestMatchers("/message/**")
                            .authenticated()
                            .anyRequest()
                            .denyAll(); })
                .addFilterBefore(ApiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return Security.build(); }
}
