package xyz.thewhitedog9487.WebAPI.Controller.Filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import xyz.thewhitedog9487.WebAPI.Data.Entity.AccessLog;
import xyz.thewhitedog9487.WebAPI.Data.Repository.AccessLogRepository;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.Locale;

@Slf4j
@Component
public class LogClientInfo extends OncePerRequestFilter {
    @Autowired AccessLogRepository AccessLogRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("接收到对于{} {} 的请求", request.getMethod(), request.getRequestURL() + ( ( request.getQueryString() == null ) ? "" : "?" + request.getQueryString() ) );
        if ( request.getHeader("CF-Connecting-IP".toLowerCase() ) instanceof String IP ) {
            /*
            ↑ 如果成功完成instanceof模式匹配，那么模式变量一定非空
            相当于是：
            if ( request.getHeader("CF-Connecting-IP".toLowerCase() ) != null ) {
                String IP = request.getHeader( "CF-Connecting-IP".toLowerCase() ); }
            */
            log.info("请求携带了CF-Connecting-IP头部，IP：{}", IP); }
        else if( request.getHeader("X-Forwarded-For".toLowerCase() ) instanceof String IP ) {
                IP = IP.split(",")[0].trim();
                log.info("请求携带了X-Forwarded-For头部，IP：{}", IP); }
        else {
            log.info("请求未携带CF-Connecting-IP和X-Forwarded-For头部，HttpServletRequest获取到的IP为：{}", request.getRemoteAddr()); }
        if ( request.getHeader("CF-IPCountry".toLowerCase() ) instanceof String IP ) {
            var Country = Locale.of(Locale.PRC.getLanguage(), IP, Locale.SIMPLIFIED_CHINESE.getVariant());
            log.info("请求携带了CF-IPCountry头部，ISO3166代码：{}，对应的国家/地区：{}", IP, Country.getDisplayCountry(Locale.SIMPLIFIED_CHINESE)); }
        else {
            log.info("请求未携带CF-IPCountry头部，无法获取国家/地区信息。"); }
        var UserAgent = request.getHeader("User-Agent");
        if( UserAgent == null ){
            log.info("请求未携带User-Agent头部，无法获取客户端软件信息。"); }
        else{
            log.info("请求的User-Agent：{}", UserAgent); }
        var Log = new AccessLog(
                null,
                request.getRequestId(),
                request.getHeader("CF-Connecting-IP".toLowerCase() ),
                request.getHeader("X-Forwarded-For".toLowerCase() ),
                request.getRemoteAddr(),
                request.getHeader("CF-IPCountry".toLowerCase() ),
                ( request.getHeader("CF-IPCountry".toLowerCase() ) == null ) ? null : Locale.of(Locale.PRC.getLanguage(), request.getHeader("CF-IPCountry".toLowerCase() ), Locale.SIMPLIFIED_CHINESE.getVariant()).getISO3Country(),
                UserAgent,
                request.getMethod(),
                request.getRequestURL().toString(),
                request.getQueryString(),
                Collections.list( request.getHeaderNames() )
                        .stream()
                        .map( name -> name + ": " + Collections.list(request.getHeaders(name)) )
                        .reduce( ( a, b ) -> a + "\n" + b )
                        .orElse(""),
                Instant.now() );
        AccessLogRepository.save(Log);
        filterChain.doFilter(request, response); } }