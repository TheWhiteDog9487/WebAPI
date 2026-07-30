package xyz.thewhitedog9487.WebAPI.Configuration

import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class SpringMvcConfiguration: WebMvcConfigurer{
    override fun configureContentNegotiation(ContentNegotiationConfigurer: ContentNegotiationConfigurer) {
        ContentNegotiationConfigurer.defaultContentType(MediaType.APPLICATION_JSON) } }