package xyz.thewhitedog9487.WebAPI.Configuration.Security

import jakarta.servlet.DispatcherType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SpringSecurityConfiguration{
    @Order(1)
    @Bean
    fun Permit(Security: HttpSecurity): SecurityFilterChain {
        return Security
            .securityMatcher("/actuator/health")
            .csrf( { CsrfConfigurer -> CsrfConfigurer.disable() } )
            .authorizeHttpRequests( { AuthorizationManagerRequestMatcherRegistry ->
                AuthorizationManagerRequestMatcherRegistry
                    .anyRequest()
                    .permitAll() } )
            .build() }

    /**
     * @see ApiKeyAuthenticationFilter#doFilterInternal(HttpServletRequest, HttpServletResponse, FilterChain)
     */
    @Order(2)
    @Bean
    fun RequireApiKey(Security: HttpSecurity): SecurityFilterChain {
        return Security
            .securityMatcher("/message/**", "/accesslog/**",
                "/actuator/**")
            .csrf( { CsrfConfigurer -> CsrfConfigurer.disable() } )
            .authorizeHttpRequests( { AuthorizationManagerRequestMatcherRegistry ->
                AuthorizationManagerRequestMatcherRegistry
                    .dispatcherTypeMatchers(DispatcherType.ASYNC)
                    .permitAll()
                    .dispatcherTypeMatchers(DispatcherType.ERROR)
                    .permitAll()
                    .anyRequest()
                    .authenticated() } )
            .addFilterBefore( ApiKeyAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .build() }

    @Order(3)
    @Bean
    fun PermitAll(Security: HttpSecurity): SecurityFilterChain {
        return Security
            .csrf( { CsrfConfigurer -> CsrfConfigurer.disable() } )
            .authorizeHttpRequests( { AuthorizationManagerRequestMatcherRegistry ->
                AuthorizationManagerRequestMatcherRegistry
                    .anyRequest()
                    .permitAll() } )
            .build() } }
