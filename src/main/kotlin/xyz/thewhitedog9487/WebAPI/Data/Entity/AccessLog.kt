package xyz.thewhitedog9487.WebAPI.Data.Entity

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.getBean
import org.springframework.web.util.ContentCachingResponseWrapper
import xyz.thewhitedog9487.WebAPI.Data.Repository.AccessLogRepository
import xyz.thewhitedog9487.WebAPI.Miscellaneous.SpringContextHolder.SpringContext
import java.time.Instant
import java.util.Locale
import java.util.concurrent.locks.ReentrantLock

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE,
    creatorVisibility = JsonAutoDetect.Visibility.NONE )
@Schema(description = "访问日志条目")
@Entity
@Table
class AccessLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @JsonProperty("ID")
    @Schema(description = "主键", example = "1")
    var ID: Long? = null,

    @JsonProperty("RequestID")
    @Schema(description = "Servlet请求ID", example = "0")
    var RequestID: String = "",

    @JsonProperty("CF_Connecting_IP")
    @Column(name = "\"CF-Connecting-IP\"")
    @Schema(description = "Cloudflare提供的请求IP", example = "2409:9802:74a3:6eff:3cae:5cba:25ac:788a")
    var CF_Connecting_IP: String? = null,

    @JsonProperty("X_Forwarded_For")
    @Column(name = "\"X-Forwarded-For\"")
    @Schema(description = "X-Forwarded-For请求头提供的请求IP", example = "2409:9802:74a3:6eff:3cae:5cba:25ac:788a, 108.162.245.89")
    var X_Forwarded_For: String? = null,

    @JsonProperty("RemoteAddress")
    @Schema(description = "Servlet报告的请求IP", example = "192.168.128.11")
    var RemoteAddress: String = "",

    @JsonProperty("CF_IPCountry")
    @Column(name = "\"CF-IPCountry\"")
    @Schema(description = "Cloudflare提供的请求国家代码", example = "CN")
    var CF_IPCountry: String? = null,

    @JsonProperty("ISO3166")
    @Schema(description = "Java内部根据CF-IPCountry得到的对应ISO3166代码", example = "CHN")
    var ISO3166: String? = null,

    @JsonProperty("UserAgent")
    @Schema(description = "标准HTTP User-Agent请求头", example = "IntelliJ HTTP Client/IntelliJ IDEA 2025.2.2")
    var UserAgent: String? = null,

    @JsonProperty("HttpMethod")
    @Schema(description = "HTTP请求方法", example = "GET")
    var HttpMethod: String = "",

    @JsonProperty("Protocol")
    @Schema(description = "请求协议", example = "http")
    var Protocol: String = "",

    @JsonProperty("ProtocolVersion")
    @Schema(description = "协议版本", example = "HTTP/1.1")
    var ProtocolVersion: String = "",

    @JsonProperty("URL")
    @Schema(description = "请求的完整URL", example = "https://dev.thewhitedog9487.xyz/ip/ip")
    var URL: String = "",

    @JsonProperty("QueryString")
    @Schema(description = "查询字符串", example = "")
    var QueryString: String? = null,

    @JsonProperty("Header")
    @Column(columnDefinition = "text")
    @Schema(
        description = "所有HTTP请求头的文本字符串", example = (
"""
host: dev.thewhitedog9487.xyz
x-real-ip: 162.158.42.207
x-forwarded-for: 2409:8962:f29:7dc:3996:6b11:d996:3cc0, 162.158.42.207
x-forwarded-proto: https
connection: close
x-api-key: asdasdaFSEA452453sdcv
user-agent: IntelliJ HTTP Client/IntelliJ IDEA 2025.2.2
accept: */*
cf-ray: 986868be1ca5def5-SEA
accept-encoding: gzip, br
cdn-loop: cloudflare; loops=1
cf-connecting-ip: 2409:8962:f29:7dc:3996:6b11:d996:3cc0
cf-ipcountry: CN
cf-visitor: {"scheme":"https"}
cookie: JSESSIONID=A49527B913170D86A5544A252BD31040
"""))
    var Header: String = "",

    @JsonProperty("Timestamp")
    @Schema(description = "请求时间 ISO-8601格式", example = "2025-09-29T03:49:42.766Z")
    var Timestamp: Instant = Instant.now(),

    @JsonProperty("ResponseStatusCode")
    @Schema(description = "响应的HTTP状态码", example = "200")
    var ResponseStatusCode: Int = 0 ){
    constructor(httpRequest: HttpServletRequest): this(
        null,
        httpRequest.requestId,
        httpRequest.getHeader("CF-Connecting-IP"),
        httpRequest.getHeader("X-Forwarded-For"),
        httpRequest.remoteAddr,
        httpRequest.getHeader("CF-IPCountry"),
        if (httpRequest.getHeader("CF-IPCountry") == null) null else Locale.of(Locale.PRC.language, httpRequest.getHeader("CF-IPCountry"), Locale.SIMPLIFIED_CHINESE.variant).isO3Country,
        httpRequest.getHeader("User-Agent"),
        httpRequest.method,
        httpRequest.scheme,
        httpRequest.protocol,
        httpRequest.requestURL.toString(),
        httpRequest.queryString,
        httpRequest.headerNames.toList().joinToString("\n") { name ->
            return@joinToString "$name: ${httpRequest.getHeaders(name).toList().joinToString(", ") }" },
        Instant.now(),
        0 )
    fun SaveIntoDatabase(CachedResponse: ContentCachingResponseWrapper? = null){
        val SQLiteWriteLock = SpringContext.getBean<ReentrantLock>("SQLiteWriteLock")
        val AccessLogRepository = SpringContext.getBean<AccessLogRepository>()
        try {
            SQLiteWriteLock.lock()
            AccessLogRepository.save(this) }
        finally {
            SQLiteWriteLock.unlock()
            CachedResponse?.copyBodyToResponse() } } }