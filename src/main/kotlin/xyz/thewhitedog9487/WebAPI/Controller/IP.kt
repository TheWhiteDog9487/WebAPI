package xyz.thewhitedog9487.WebAPI.Controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name="请求客户端IP地址相关")
@RestController
@RequestMapping("/ip")
class IP {
    @Operation(summary = "获取请求客户端的IP地址", description =
"""
优先级：  
1. CF-Connecting-IP  
2. X-Forwarded-For  
3. 直接使用请求的远程地址""")
    @ApiResponse(
        responseCode = "200",
        description = "成功获取到IP地址，内容为纯文本格式的IP地址",
        content = [Content(
            mediaType = MediaType.TEXT_PLAIN_VALUE,
            schema = Schema(implementation = String::class),
            examples = [ExampleObject(value = "78.141.226.247") ] ) ] )
    @GetMapping("")
    fun GetIP(@RequestHeader("CF-Connecting-IP", required = false) Header_CFConnectingIP: String?,
              @RequestHeader("X-Forwarded-For", required = false) Header_XForwardedFor: String?,
              Request: HttpServletRequest): ResponseEntity<String> {
        Header_CFConnectingIP?.let {
            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(Header_CFConnectingIP) }
        Header_XForwardedFor?.let {
            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(Header_XForwardedFor) }
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_PLAIN)
            .body(Request.remoteAddr) }

    @Operation(summary = "获取请求客户端的ISO 3166-1 alpha-2国家代码", description =
"""
依赖Cloudflare的CF-IPCountry头部  
如果请求没有经过Cloudflare，则会返回"未找到CF-IPCountry头部"
""")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "成功获取到ISO 3166-1 alpha-2国家代码，内容为纯文本格式的国家代码",
            content = [Content(
                mediaType = MediaType.TEXT_PLAIN_VALUE,
                schema = Schema(implementation = String::class),
                examples = [ExampleObject(value = "HK") ] ) ] ),
        ApiResponse(
            responseCode = "404",
            description = "未找到CF-IPCountry头部，内容为纯文本格式的错误信息",
            content = [Content(
                mediaType = MediaType.TEXT_PLAIN_VALUE,
                schema = Schema(implementation = String::class),
                examples = [ExampleObject(value = "未找到CF-IPCountry头部") ] ) ] ) ] )
    @GetMapping("iso3166")
    fun GetIso3166(@RequestHeader("CF-IPCountry", required = false) Header_CFIPCountry: String?): ResponseEntity<String> {
        Header_CFIPCountry?.let {
            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(Header_CFIPCountry) }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.TEXT_PLAIN)
            .body("未找到CF-IPCountry头部") } }