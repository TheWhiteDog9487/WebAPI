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
import java.util.concurrent.locks.Lock;

@Slf4j
@Component
public class LogClientInfo implements Filter {
    public static List<String> IgnorePaths = List.of(
            "/favicon.ico",
            "/swagger-ui",
            "/v3/api-docs",
            "/accesslog");

    @Autowired AccessLogRepository AccessLogRepository;
    @Autowired Lock SQLiteWriteLock;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest HttpServletRequest = (HttpServletRequest) request;
        HttpServletResponse HttpServletResponse = (HttpServletResponse) response;
        var Response = new ContentCachingResponseWrapper(HttpServletResponse);
        var Log = new AccessLog(
                null,
                HttpServletRequest.getRequestId(),
                HttpServletRequest.getHeader("CF-Connecting-IP"),
                HttpServletRequest.getHeader("X-Forwarded-For"),
                HttpServletRequest.getRemoteAddr(),
                HttpServletRequest.getHeader("CF-IPCountry"),
                ( HttpServletRequest.getHeader("CF-IPCountry") == null ) ? null : Locale.of(Locale.PRC.getLanguage(), HttpServletRequest.getHeader("CF-IPCountry"), Locale.SIMPLIFIED_CHINESE.getVariant()).getISO3Country(),
                HttpServletRequest.getHeader("User-Agent" ),
                HttpServletRequest.getMethod(),
                HttpServletRequest.getScheme(),
                HttpServletRequest.getProtocol(),
                HttpServletRequest.getRequestURL().toString(),
                HttpServletRequest.getQueryString(),
                Collections.list( HttpServletRequest.getHeaderNames() )
                        .stream()
                        .map( name -> name + ": " + Collections.list(HttpServletRequest.getHeaders(name)).getFirst() )
                        .reduce( ( a, b ) -> a + "\n" + b )
                        .orElse(""),
                Instant.now(),
                null,
                null );
        try {
            SQLiteWriteLock.lock();
            Log = AccessLogRepository.save(Log); }
        catch (Exception e) {
            log.error(e.getLocalizedMessage()); }
        finally {
            SQLiteWriteLock.unlock(); }
        chain.doFilter(request, Response);
        var ResponseBody = IgnorePaths.stream()
                .anyMatch( path -> HttpServletRequest.getRequestURI().startsWith(path) )
                ? null : new String( Response.getContentAsByteArray(), Response.getCharacterEncoding() );
        Log.setResponseStatusCode(HttpServletResponse.getStatus());
        Log.setResponseBody(ResponseBody);
        try {
            SQLiteWriteLock.lock();
            AccessLogRepository.save(Log); }
        catch (Exception e) {
            log.error(e.getLocalizedMessage()); }
        finally {
            SQLiteWriteLock.unlock(); }
        Response.copyBodyToResponse(); } }