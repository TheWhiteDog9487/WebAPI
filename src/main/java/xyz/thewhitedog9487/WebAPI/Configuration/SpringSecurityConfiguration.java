package xyz.thewhitedog9487.WebAPI.Configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import xyz.thewhitedog9487.WebAPI.Configuration.Security.ApiKeyAuthenticationFilter;
import xyz.thewhitedog9487.WebAPI.Data.Repository.AccessLogRepository;

import java.util.List;

@Configuration
class SpringSecurityConfiguration {

    @Autowired List<String> ApiKeyList;
    @Autowired AccessLogRepository AccessLogRepository;

    /**
     * @see ApiKeyAuthenticationFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)
     */
    @Order(1)
    @Bean
    SecurityFilterChain RequireAPIKey(HttpSecurity Security) throws Exception {
        Security
                .securityMatcher("/message/**", "/accesslog/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(AuthorizationManagerRequestMatcherRegistry -> {
                    AuthorizationManagerRequestMatcherRegistry
                            .anyRequest()
                            .authenticated(); })
                .addFilterBefore(new ApiKeyAuthenticationFilter(ApiKeyList, AccessLogRepository), UsernamePasswordAuthenticationFilter.class);
        return Security.build(); }

    @Order(2)
    @Bean
    SecurityFilterChain PermitAll(HttpSecurity Security) throws Exception {
        Security
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(AuthorizationManagerRequestMatcherRegistry -> {
                    AuthorizationManagerRequestMatcherRegistry
                            .anyRequest()
                            .permitAll(); });
        return Security.build(); }
}