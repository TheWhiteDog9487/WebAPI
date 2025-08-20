package xyz.thewhitedog9487.WebAPI.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Tag(name="请求客户端IP地址相关")
@Slf4j
@RestController
@RequestMapping("/ip")
class IP {
    @Operation(summary = "获取请求客户端的IP地址", description = """
            优先级：
            1. CF-Connecting-IP
            2. X-Forwarded-For
            3. 直接使用请求的远程地址
            """)
    @ApiResponse(responseCode = "200",
            content = @Content(
                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                    schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(value = "78.141.226.247")),
            description = "成功获取到IP地址，内容为纯文本格式的IP地址")
    @GetMapping("/ip")
    ResponseEntity<String> GetIP(@RequestHeader Map<String, String> HttpHeader, HttpServletRequest Request) {
        if ( HttpHeader.get("CF-Connecting-IP".toLowerCase() ) instanceof String IP) {
            /*
            ↑ 如果成功完成instanceof模式匹配，那么模式变量一定非空
            相当于是：
            if ( HttpHeader.get("CF-Connecting-IP".toLowerCase() ) != null ) {
                String IP = HttpHeader.get( "CF-Connecting-IP".toLowerCase() ); }
            */
            return new ResponseEntity<>(IP,
                    MultiValueMap.fromSingleValue(Map.of("Content-Type", "text/plain;charset=UTF-8")),
                    HttpStatus.OK); }
        else if (HttpHeader.get("X-Forwarded-For".toLowerCase()) instanceof String IP) {
                IP = IP.split(",")[0].trim();
                return new ResponseEntity<>(IP,
                        MultiValueMap.fromSingleValue(Map.of("Content-Type", "text/plain;charset=UTF-8")),
                        HttpStatus.OK); }
        else {
            return new ResponseEntity<>(Request.getRemoteAddr(),
                    MultiValueMap.fromSingleValue(Map.of("Content-Type", "text/plain;charset=UTF-8")),
                    HttpStatus.OK); } }

    @Operation(summary = "获取请求客户端的ISO 3166-1 alpha-2国家代码", description = """
            依赖Cloudflare的CF-IPCountry头部
            <br>
            如果请求没有经过Cloudflare，则会返回"未找到CF-IPCountry头部"
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class),
                            examples = @ExampleObject(value = "HK") ),
                    description = "成功获取到ISO 3166-1 alpha-2国家代码，内容为纯文本格式的国家代码"),
            @ApiResponse(responseCode = "404",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class),
                            examples = { @ExampleObject(value = "未找到CF-IPCountry头部") } ),
                    description = "未找到CF-IPCountry头部，内容为纯文本格式的错误信息") } )
    @GetMapping("iso3166")
    ResponseEntity<String> GetISO3166(@RequestHeader Map<String, String> HttpHeader) {
        if (HttpHeader.get("CF-IPCountry".toLowerCase()) instanceof String CountryCode) {
            return new ResponseEntity<>(CountryCode,
                    MultiValueMap.fromSingleValue(Map.of("Content-Type", "text/plain;charset=UTF-8")),
                    HttpStatus.OK); }
        else {
            log.warn("请求尝试获取其ip对应的iso3166代码，但是我们没有找到CF-IPCountry头部");
            return new ResponseEntity<>("未找到CF-IPCountry头部",
                    MultiValueMap.fromSingleValue(Map.of("Content-Type", "text/plain;charset=UTF-8")),
                    HttpStatus.NOT_FOUND); } }
}
