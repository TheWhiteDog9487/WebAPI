package xyz.thewhitedog9487.WebAPI.Data.Entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
@Schema(description = "访问日志条目")
public class AccessLog{
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        @JsonProperty("ID")
        @Schema(description = "主键", example = "1")
        Long ID;

        @JsonProperty("RequestID")
        @Schema(description = "Servlet请求ID", example = "0")
        String RequestID;

        @JsonProperty("CF_Connecting_IP")
        @Column(name = "\"CF-Connecting-IP\"")
        @Schema(description = "Cloudflare提供的请求IP", example = "2409:9802:74a3:6eff:3cae:5cba:25ac:788a")
        String CF_Connecting_IP;

        @JsonProperty("X_Forwarded_For")
        @Column(name = "\"X-Forwarded-For\"")
        @Schema(description = "X-Forwarded-For请求头提供的请求IP", example = "2409:9802:74a3:6eff:3cae:5cba:25ac:788a, 108.162.245.89")
        String X_Forwarded_For;

        @JsonProperty("RemoteAddress")
        @Schema(description = "Servlet报告的请求IP", example = "192.168.128.11")
        String RemoteAddress;

        @JsonProperty("CF_IPCountry")
        @Column(name = "\"CF-IPCountry\"")
        @Schema(description = "Cloudflare提供的请求国家代码", example = "CN")
        String CF_IPCountry;

        @JsonProperty("ISO3166")
        @Schema(description = "Java内部根据CF-IPCountry得到的对应ISO3166代码", example = "CHN")
        String ISO3166;

        @JsonProperty("UserAgent")
        @Schema(description = "标准HTTP User-Agent请求头", example = "IntelliJ HTTP Client/IntelliJ IDEA 2025.2.2")
        String UserAgent;

        @JsonProperty("HttpMethod")
        @Schema(description = "HTTP请求方法", example = "GET")
        String HttpMethod;

        @JsonProperty("Protocol")
        @Schema(description = "请求协议", example = "http")
        String Protocol;

        @JsonProperty("ProtocolVersion")
        @Schema(description = "协议版本", example = "HTTP/1.1")
        String ProtocolVersion;

        @JsonProperty("URL")
        @Schema(description = "请求的完整URL", example = "http://dev.thewhitedog9487.xyz/ip/ip")
        String URL;

        @JsonProperty("QueryString")
        @Schema(description = "查询字符串", example = "")
        String QueryString;

        @JsonProperty("Header")
        @Column(columnDefinition = "text")
        @Schema(description = "所有HTTP请求头的JSON化字符串", example = "host: dev.thewhitedog9487.xyz\n" +
                "x-real-ip: 108.42.101.215\n" +
                "x-forwarded-for: 2409:3e5a:642:1f64:a9d4:c96c:72d1:88ef, 108.42.101.215\n" +
                "x-forwarded-proto: https\n" +
                "connection: close\n" +
                "x-api-key: 985651Fardsh6452453sdcv\n" +
                "user-agent: IntelliJ HTTP Client/IntelliJ IDEA 2025.2.3\n" +
                "accept: */*\n" +
                "cf-ray: 98b487dc1dead465-SEA\n" +
                "accept-encoding: gzip, br\n" +
                "cdn-loop: cloudflare; loops=1\n" +
                "cf-connecting-ip: 2409:3e5a:642:1f64:a9d4:c96c:72d1:88ef\n" +
                "cf-ipcountry: CN\n" +
                "cf-visitor: {\"scheme\":\"https\"}\n" +
                "cookie: JSESSIONID=59DB3634C2FB313AFFEE54D4F933F101")
        String Header;

        @JsonProperty("Timestamp")
        @Schema(description = "请求时间的Unix时间戳", example = "1758437539600")
        Instant Timestamp;

        @JsonProperty("ResponseStatusCode")
        @Schema(description = "响应的HTTP状态码", example = "200")
        Integer ResponseStatusCode;

        @JsonProperty("ResponseBody")
        @Column(columnDefinition = "text")
        @Schema(description = "响应内容", example = "")
        String ResponseBody;
}