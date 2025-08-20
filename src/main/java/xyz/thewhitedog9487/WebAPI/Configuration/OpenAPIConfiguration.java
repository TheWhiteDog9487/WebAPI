package xyz.thewhitedog9487.WebAPI.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
class OpenAPIConfiguration {
    @Bean
    OpenAPI CustomOpenAPIConfiguration() {
        return new OpenAPI()
                .info(new Info()
                        .title("TheWhiteDog9487的通用API们")
                        .summary("")
                        .description("")
                        .termsOfService("")
                        .contact(new Contact()
                                .name("TheWhiteDog9487")
                                .url("https://www.github.com/TheWhiteDog9487/WebAPI"))
                        .version("0.4.0")
                        .license(new License()
                                .name("WTFPL")
                                .url("https://spdx.org/licenses/WTFPL")) )
                .servers(List.of(
                        new Server()
                                .url("https://api.thewhitedog9487.xyz")
                                .description("主服务器"),
                        new Server()
                                .url("http://localhost:12345")
                                .description("本地开发服务器"),
                        new  Server()
                                .url("https://dev.thewhitedog9487.xyz")
                                .description("本地开发服务器（经过Cloudflare）") ) );} }