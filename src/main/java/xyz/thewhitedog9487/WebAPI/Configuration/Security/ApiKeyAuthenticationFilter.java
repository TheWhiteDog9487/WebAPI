package xyz.thewhitedog9487.WebAPI.Configuration.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import xyz.thewhitedog9487.WebAPI.Controller.ResponseData;
import xyz.thewhitedog9487.WebAPI.Data.Entity.AccessLog;
import xyz.thewhitedog9487.WebAPI.Data.Repository.AccessLogRepository;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    @Autowired List<String> ApiKeyList;
    @Autowired AccessLogRepository AccessLogRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        var ServletPath = request.getServletPath();
        var PermitPrefix = List.of(
                "/ip/",
                "/v3/api-docs",
                "/swagger-ui/" );
        var FullyMatchList = List.of(
                "/",
                "/swagger-ui.html" );
        for (String Prefix : PermitPrefix) {
            if ( ServletPath.startsWith(Prefix) ) {
                return true; } }
        for (String FullyMatch : FullyMatchList) {
            if ( ServletPath.equals(FullyMatch) ) {
                return true; } }
        return false; }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ApiKey = request.getHeader("X-API-Key");
        if (ApiKey == null) {
            var ResponseBody = new ResponseData(
                    HttpStatus.UNAUTHORIZED.value(),
                    "API密钥验证失败，未传递X-API-Key请求头");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(ResponseBody.ToJson());
            var Log = new AccessLog(
                    null,
                    request.getRequestId(),
                    request.getHeader("CF-Connecting-IP".toLowerCase() ),
                    request.getHeader("X-Forwarded-For".toLowerCase() ),
                    request.getRemoteAddr(),
                    request.getHeader("CF-IPCountry".toLowerCase() ),
                    ( request.getHeader("CF-IPCountry".toLowerCase() ) == null ) ? null : Locale.of(Locale.PRC.getLanguage(), request.getHeader("CF-IPCountry".toLowerCase() ), Locale.SIMPLIFIED_CHINESE.getVariant()).getISO3Country(),
                    request.getHeader("User-Agent"),
                    request.getMethod(),
                    request.getScheme(),
                    request.getProtocol(),
                    request.getRequestURL().toString(),
                    request.getQueryString(),
                    Collections.list( request.getHeaderNames() )
                            .stream()
                            .map( name -> name + ": " + Collections.list(request.getHeaders(name)) )
                            .reduce( ( a, b ) -> a + "\n" + b )
                            .orElse(""),
                    Instant.now(),
                    HttpStatus.UNAUTHORIZED.value(),
                    ResponseBody.ToJson());
            AccessLogRepository.save(Log);
            return; }
        if (ApiKeyList.contains(ApiKey) == true) {
            var Auth = new UsernamePasswordAuthenticationToken(
                    "",
                    ApiKey,
                    List.of(new SimpleGrantedAuthority("API")));
            SecurityContextHolder.getContext().setAuthentication(Auth);
            filterChain.doFilter(request, response);
        } else {
            var ResponseBody = new ResponseData(
                    HttpStatus.UNAUTHORIZED.value(),
                    "API密钥验证失败",
                    Map.of("提供的密钥:", ApiKey));
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(ResponseBody.ToJson());
            var Log = new AccessLog(
                    null,
                    request.getRequestId(),
                    request.getHeader("CF-Connecting-IP".toLowerCase() ),
                    request.getHeader("X-Forwarded-For".toLowerCase() ),
                    request.getRemoteAddr(),
                    request.getHeader("CF-IPCountry".toLowerCase() ),
                    ( request.getHeader("CF-IPCountry".toLowerCase() ) == null ) ? null : Locale.of(Locale.PRC.getLanguage(), request.getHeader("CF-IPCountry".toLowerCase() ), Locale.SIMPLIFIED_CHINESE.getVariant()).getISO3Country(),
                    request.getHeader("User-Agent"),
                    request.getMethod(),
                    request.getScheme(),
                    request.getProtocol(),
                    request.getRequestURL().toString(),
                    request.getQueryString(),
                    Collections.list( request.getHeaderNames() )
                            .stream()
                            .map( name -> name + ": " + Collections.list(request.getHeaders(name)) )
                            .reduce( ( a, b ) -> a + "\n" + b )
                            .orElse(""),
                    Instant.now(),
                    HttpStatus.UNAUTHORIZED.value(),
                    ResponseBody.ToJson() );
            AccessLogRepository.save(Log);
            return; } } }

