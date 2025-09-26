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
import java.util.Locale;

@Slf4j
@Component
public class LogClientInfo implements Filter {
    @Autowired AccessLogRepository AccessLogRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest HttpServletRequest = (HttpServletRequest) request;
        HttpServletResponse HttpServletResponse = (HttpServletResponse) response;
        var Response = new ContentCachingResponseWrapper(HttpServletResponse);
        log.info("接收到对于{} {} 的请求", HttpServletRequest.getMethod(), HttpServletRequest.getRequestURL() + ( ( HttpServletRequest.getQueryString() == null ) ? "" : "?" + HttpServletRequest.getQueryString() ) );
        if ( HttpServletRequest.getHeader("CF-Connecting-IP".toLowerCase() ) instanceof String IP ) {
            /*
            ↑ 如果成功完成instanceof模式匹配，那么模式变量一定非空
            相当于是：
            if ( HttpServletRequest.getHeader("CF-Connecting-IP".toLowerCase() ) != null ) {
                String IP = HttpServletRequest.getHeader( "CF-Connecting-IP".toLowerCase() ); }
            */
            log.info("请求携带了CF-Connecting-IP头部，IP：{}", IP); }
        else if( HttpServletRequest.getHeader("X-Forwarded-For".toLowerCase() ) instanceof String IP ) {
                IP = IP.split(",")[0].trim();
                log.info("请求携带了X-Forwarded-For头部，IP：{}", IP); }
        else {
            log.info("请求未携带CF-Connecting-IP和X-Forwarded-For头部，HttpServletRequest获取到的IP为：{}", HttpServletRequest.getRemoteAddr()); }
        if ( HttpServletRequest.getHeader("CF-IPCountry".toLowerCase() ) instanceof String IP ) {
            var Country = Locale.of(Locale.PRC.getLanguage(), IP, Locale.SIMPLIFIED_CHINESE.getVariant());
            log.info("请求携带了CF-IPCountry头部，ISO3166代码：{}，对应的国家/地区：{}", IP, Country.getDisplayCountry(Locale.SIMPLIFIED_CHINESE)); }
        else {
            log.info("请求未携带CF-IPCountry头部，无法获取国家/地区信息。"); }
        var UserAgent = HttpServletRequest.getHeader("User-Agent");
        if( UserAgent == null ){
            log.info("请求未携带User-Agent头部，无法获取客户端软件信息。"); }
        else{
            log.info("请求的User-Agent：{}", UserAgent); }
        var Log = new AccessLog(
                null,
                HttpServletRequest.getRequestId(),
                HttpServletRequest.getHeader("CF-Connecting-IP".toLowerCase() ),
                HttpServletRequest.getHeader("X-Forwarded-For".toLowerCase() ),
                HttpServletRequest.getRemoteAddr(),
                HttpServletRequest.getHeader("CF-IPCountry".toLowerCase() ),
                ( HttpServletRequest.getHeader("CF-IPCountry".toLowerCase() ) == null ) ? null : Locale.of(Locale.PRC.getLanguage(), HttpServletRequest.getHeader("CF-IPCountry".toLowerCase() ), Locale.SIMPLIFIED_CHINESE.getVariant()).getISO3Country(),
                UserAgent,
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
        var ResponseBody = new String( Response.getContentAsByteArray(), Response.getCharacterEncoding() );
        Log.setResponseStatusCode(HttpServletResponse.getStatus());
        Log.setResponseBody(ResponseBody);
        AccessLogRepository.save(Log);
        log.info("请求处理完成，响应状态码：{}", HttpServletResponse.getStatus());
        Response.copyBodyToResponse(); } }