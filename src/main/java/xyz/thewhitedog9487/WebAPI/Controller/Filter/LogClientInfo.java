package xyz.thewhitedog9487.WebAPI.Controller.Filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;
import xyz.thewhitedog9487.WebAPI.Data.Entity.AccessLog;
import xyz.thewhitedog9487.WebAPI.Data.Repository.AccessLogRepository;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
public class LogClientInfo implements Filter {
    public static List<String> IgnorePaths = List.of(
            "/favicon.ico",
            "/swagger-ui",
            "/v3/api-docs" );

    @Autowired AccessLogRepository AccessLogRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest HttpServletRequest = (HttpServletRequest) request;
        HttpServletResponse HttpServletResponse = (HttpServletResponse) response;
        var Response = new ContentCachingResponseWrapper(HttpServletResponse);
        var Log = new AccessLog(
                null,
                HttpServletRequest.getRequestId(),
                HttpServletRequest.getHeader("CF-Connecting-IP".toLowerCase() ),
                HttpServletRequest.getHeader("X-Forwarded-For".toLowerCase() ),
                HttpServletRequest.getRemoteAddr(),
                HttpServletRequest.getHeader("CF-IPCountry".toLowerCase() ),
                ( HttpServletRequest.getHeader("CF-IPCountry".toLowerCase() ) == null ) ? null : Locale.of(Locale.PRC.getLanguage(), HttpServletRequest.getHeader("CF-IPCountry".toLowerCase() ), Locale.SIMPLIFIED_CHINESE.getVariant()).getISO3Country(),
                HttpServletRequest.getHeader("User-Agent" ),
                HttpServletRequest.getMethod(),
                HttpServletRequest.getScheme(),
                HttpServletRequest.getProtocol(),
                HttpServletRequest.getRequestURL().toString(),
                HttpServletRequest.getQueryString(),
                Collections.list( HttpServletRequest.getHeaderNames() )
                        .stream()
                        .map( name -> name + ": " + Collections.list(HttpServletRequest.getHeaders(name)) )
                        .reduce( ( a, b ) -> a + "\n" + b )
                        .orElse(""),
                Instant.now(),
                null,
                null );
        Log = AccessLogRepository.save(Log);
        chain.doFilter(request, Response);
        var ResponseBody = IgnorePaths.stream()
                .anyMatch( path -> HttpServletRequest.getRequestURI().startsWith(path) )
                ? null : new String( Response.getContentAsByteArray(), Response.getCharacterEncoding() );
        Log.setResponseStatusCode(HttpServletResponse.getStatus());
        Log.setResponseBody(ResponseBody);
        AccessLogRepository.save(Log);
        Response.copyBodyToResponse(); } }