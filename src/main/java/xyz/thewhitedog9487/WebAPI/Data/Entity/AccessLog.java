package xyz.thewhitedog9487.WebAPI.Data.Entity;

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
        Long ID;

        String RequestID;
        @Column(name = "\"CF-Connecting-IP\"") String CF_Connecting_IP;
        @Column(name = "\"X-Forwarded-For\"") String X_Forwarded_For;
        String RemoteAddress;
        @Column(name = "\"CF-IPCountry\"") String CF_IPCountry;
        String ISO3166;
        String UserAgent;
        String HttpMethod;
        String URL;
        String QueryString;
        @Column(columnDefinition = "text") String Header;
        Instant Timestamp; }