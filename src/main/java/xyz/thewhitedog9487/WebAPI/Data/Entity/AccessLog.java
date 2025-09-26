package xyz.thewhitedog9487.WebAPI.Data.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class AccessLog{
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(nullable = false)
        @JsonProperty("ID")
        Long ID;

        @JsonProperty("RequestID")
        String RequestID;

        @JsonProperty("CF_Connecting_IP")
        @Column(name = "\"CF-Connecting-IP\"")
        String CF_Connecting_IP;

        @JsonProperty("X_Forwarded_For")
        @Column(name = "\"X-Forwarded-For\"")
        String X_Forwarded_For;

        @JsonProperty("RemoteAddress")
        String RemoteAddress;

        @JsonProperty("CF_IPCountry")
        @Column(name = "\"CF-IPCountry\"")
        String CF_IPCountry;

        @JsonProperty("ISO3166")
        String ISO3166;

        @JsonProperty("UserAgent")
        String UserAgent;

        @JsonProperty("HttpMethod")
        String HttpMethod;

        @JsonProperty("Protocol")
        String Protocol;

        @JsonProperty("ProtocolVersion")
        String ProtocolVersion;

        @JsonProperty("URL")
        String URL;

        @JsonProperty("QueryString")
        String QueryString;

        @JsonProperty("Header")
        @Column(columnDefinition = "text")
        String Header;

        @JsonProperty("Timestamp")
        Instant Timestamp;

        @JsonProperty("ResponseStatusCode")
        Integer ResponseStatusCode;

        @JsonProperty("ResponseBody")
        @Column(columnDefinition = "text")
        String ResponseBody;
}