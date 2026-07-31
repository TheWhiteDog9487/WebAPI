package xyz.thewhitedog9487.WebAPI.Configuration

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfiguration {
    @Bean
    fun CustomOpenAPIConfiguration(): OpenAPI {
        return OpenAPI()
                .info(Info()
                        .title("TheWhiteDog9487的通用API们")
                        .summary("")
                        .description("")
                        .termsOfService("")
                        .contact(Contact()
                                .name("TheWhiteDog9487")
                                .url("https://www.github.com/TheWhiteDog9487/WebAPI"))
                        .version("0.8.3")
                        .license(License()
                                .name("WTFPL")
                                .url("https://spdx.org/licenses/WTFPL")) )
                .servers(listOf<Server>(
                        Server()
                                .url("https://api.thewhitedog9487.xyz")
                                .description("主服务器") ) ) } }