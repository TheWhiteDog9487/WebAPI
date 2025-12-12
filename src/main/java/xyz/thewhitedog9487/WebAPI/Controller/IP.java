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
import org.springframework.http.HttpHeaders;
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
    ResponseEntity<String> GetIP(@RequestHeader("CF-COnnecting-IP") String Header_CFConnectingIP,
                                 @RequestHeader("X-Forwarded-For") String Header_XForwardedFor,
                                 HttpServletRequest Request) {
        if (Header_CFConnectingIP != null) {
            return new ResponseEntity<>(Header_CFConnectingIP,
                    new HttpHeaders(MultiValueMap.fromSingleValue(Map.of("Content-Type", "text/plain;charset=UTF-8"))),
                    HttpStatus.OK); }
        else if (Header_XForwardedFor != null) {
                Header_XForwardedFor = Header_XForwardedFor.split(",")[0].trim();
                return new ResponseEntity<>(Header_XForwardedFor,
                        new HttpHeaders(MultiValueMap.fromSingleValue(Map.of("Content-Type", "text/plain;charset=UTF-8"))),
                        HttpStatus.OK); }
        else {
            return new ResponseEntity<>(Request.getRemoteAddr(),
                    new HttpHeaders(MultiValueMap.fromSingleValue(Map.of("Content-Type", "text/plain;charset=UTF-8"))),
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
    ResponseEntity<String> GetISO3166(@RequestHeader("CF-IPCountry") String Header_CFIPCountry) {
        if (Header_CFIPCountry != null) {
            return new ResponseEntity<>(Header_CFIPCountry,
                    new HttpHeaders(MultiValueMap.fromSingleValue(Map.of("Content-Type", "text/plain;charset=UTF-8"))),
                    HttpStatus.OK); }
        else {
            return new ResponseEntity<>("未找到CF-IPCountry头部",
                    new HttpHeaders(MultiValueMap.fromSingleValue(Map.of("Content-Type", "text/plain;charset=UTF-8"))),
                    HttpStatus.NOT_FOUND); } }
}
