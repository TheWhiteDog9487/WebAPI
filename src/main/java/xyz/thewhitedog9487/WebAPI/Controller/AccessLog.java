package xyz.thewhitedog9487.WebAPI.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.thewhitedog9487.WebAPI.Data.Repository.AccessLogRepository;

import java.util.List;

@Tag(name = "日志相关")
@RestController
@RequestMapping("/accesslog")
class AccessLog {
    @Autowired AccessLogRepository AccessLogRepository;

    @Operation(summary = "获取访问日志数据库内所有的记录",
            description = "这是一个私有API，需要在请求头中提供X-API-Key以进行身份验证")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "成功获取到数据",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    minItems = 0,
                                    schema = @Schema(
                                            implementation = xyz.thewhitedog9487.WebAPI.Data.Entity.AccessLog.class ) ),
                            examples = @ExampleObject(value = """
                            [
                              {
                                "ID": 18,
                                "RequestID": "3",
                                "CF_Connecting_IP": "223.87.14.146",
                                "X_Forwarded_For": "223.87.14.146, 141.101.99.156",
                                "RemoteAddress": "192.168.128.11",
                                "CF_IPCountry": "CN",
                                "ISO3166": "CHN",
                                "UserAgent": "Mozilla/5.0 (Linux; Android 10; SM-A202F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.101 Mobile Safari/537.36",
                                "HttpMethod": "GET",
                                "Protocol": "http",
                                "ProtocolVersion": "HTTP/1.0",
                                "URL": "http://dev.thewhitedog9487.xyz/",
                                "QueryString": null,
                                "Header": "host: [dev.thewhitedog9487.xyz]\\nx-real-ip: [141.101.99.156]\\nx-forwarded-for: [223.87.14.146, 141.101.99.156]\\nx-forwarded-proto: [https]\\nconnection: [close]\\ncf-ray: [986883191fd5ed0b-LHR]\\nuser-agent: [Mozilla/5.0 (Linux; Android 10; SM-A202F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.101 Mobile Safari/537.36]\\naccept-encoding: [gzip, br]\\nreferer: [https://www.baidu.com]\\ncdn-loop: [cloudflare; loops=1]\\ncf-connecting-ip: [223.87.14.146]\\ncf-ipcountry: [CN]\\ncf-visitor: [{\\"scheme\\":\\"https\\"}]",
                                "Timestamp": "2025-09-29T03:49:42.766Z",
                                "ResponseStatusCode": 302,
                                "ResponseBody": ""
                              },
                              {
                                "ID": 261,
                                "RequestID": "31",
                                "CF_Connecting_IP": "2409:8962:77a:49c:a579:f5af:58a3:e2c",
                                "X_Forwarded_For": "2409:8962:77a:49c:a579:f5af:58a3:e2c, 172.71.150.157",
                                "RemoteAddress": "192.168.128.11",
                                "CF_IPCountry": "CN",
                                "ISO3166": "CHN",
                                "UserAgent": "IntelliJ HTTP Client/IntelliJ IDEA 2025.2.3",
                                "HttpMethod": "GET",
                                "Protocol": "http",
                                "ProtocolVersion": "HTTP/1.0",
                                "URL": "http://dev.thewhitedog9487.xyz/accesslog",
                                "QueryString": null,
                                "Header": "host: dev.thewhitedog9487.xyz\\nx-real-ip: 172.71.150.157\\nx-forwarded-for: 2409:8962:77a:49c:a579:f5af:58a3:e2c, 172.71.150.157\\nx-forwarded-proto: https\\nconnection: close\\nx-api-key: asdasdaFSEA452453sdcv\\nuser-agent: IntelliJ HTTP Client/IntelliJ IDEA 2025.2.3\\naccept: */*\\ncf-ray: 98cc7d6aaea1c37a-SEA\\naccept-encoding: gzip, br\\ncdn-loop: cloudflare; loops=1\\ncf-connecting-ip: 2409:8962:77a:49c:a579:f5af:58a3:e2c\\ncf-ipcountry: CN\\ncf-visitor: {\\"scheme\\":\\"https\\"}\\ncookie: JSESSIONID=A49527B913170D86A5544A252BD31040",
                                "Timestamp": "2025-10-11T07:02:05.718Z",
                                "ResponseStatusCode": 200,
                                "ResponseBody": null
                              },
                              {
                                "ID": 263,
                                "RequestID": "35",
                                "CF_Connecting_IP": "2409:8962:77a:49c:a579:f5af:58a3:e2c",
                                "X_Forwarded_For": "2409:8962:77a:49c:a579:f5af:58a3:e2c, 108.162.246.209",
                                "RemoteAddress": "192.168.128.11",
                                "CF_IPCountry": "CN",
                                "ISO3166": "CHN",
                                "UserAgent": "IntelliJ HTTP Client/IntelliJ IDEA 2025.2.3",
                                "HttpMethod": "GET",
                                "Protocol": "http",
                                "ProtocolVersion": "HTTP/1.0",
                                "URL": "http://dev.thewhitedog9487.xyz/ip/ip",
                                "QueryString": null,
                                "Header": "host: dev.thewhitedog9487.xyz\\nx-real-ip: 108.162.246.209\\nx-forwarded-for: 2409:8962:77a:49c:a579:f5af:58a3:e2c, 108.162.246.209\\nx-forwarded-proto: https\\nconnection: close\\naccept-encoding: gzip, br\\nuser-agent: IntelliJ HTTP Client/IntelliJ IDEA 2025.2.3\\naccept: */*\\ncf-ray: 98cc9ec12c48a1a5-SEA\\ncdn-loop: cloudflare; loops=1\\ncf-connecting-ip: 2409:8962:77a:49c:a579:f5af:58a3:e2c\\ncf-ipcountry: CN\\ncf-visitor: {\\"scheme\\":\\"https\\"}\\ncookie: JSESSIONID=A49527B913170D86A5544A252BD31040",
                                "Timestamp": "2025-10-11T07:24:51.504Z",
                                "ResponseStatusCode": 200,
                                "ResponseBody": "2409:8962:77a:49c:a579:f5af:58a3:e2c"
                              }
                            ]
                            """) ) ),
            @ApiResponse(responseCode = "401",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseData.class),
                            examples = {
                                    @ExampleObject(value = "{\n" +
                                            "  \"code\": 401,\n" +
                                            "  \"message\": \"请求缺少必要的头部信息\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"缺失的头部\": \"X-API-Key\"\n" +
                                            "  }\n" +
                                            "}", name = "缺少头部信息"),
                                    @ExampleObject(value = "{\n" +
                                            "  \"code\": 401,\n" +
                                            "  \"message\": \"API密钥验证失败\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"提供的密钥\": \"123456\"\n" +
                                            "  }\n" +
                                            "}", name = "密钥不正确") } ),
                    description = "API密钥验证失败") } )
    @GetMapping
    List<xyz.thewhitedog9487.WebAPI.Data.Entity.AccessLog> GetFullLog(
            @Parameter(description = "用于身份验证的API密钥", in = ParameterIn.HEADER, required = true, example = "ds1858dscc8745sfwe")
            @RequestHeader("X-API-Key") String ApiKey){
        return AccessLogRepository.findAll(); }
}
