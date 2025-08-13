package xyz.thewhitedog9487.WebAPI.Controller.Filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class LogClientInfo extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("接收到对于{} {} 的请求", request.getMethod(), request.getRequestURL() + ( ( request.getQueryString() == null ) ? "" : "?" + request.getQueryString() ) );
        log.info("请求ID：{}", request.getRequestId());
        if (request.getHeader("CF-Connecting-IP".toLowerCase()) instanceof String IP) {
            log.info("请求携带了CF-Connecting-IP头部，IP为：{}", IP); }
        else if(request.getHeader("X-Forwarded-For".toLowerCase()) instanceof String IP) {
                IP = IP.split(",")[0].trim();
                log.info("请求携带了X-Forwarded-For头部，IP为：{}", IP); }
        else {
            log.info("请求未携带CF-Connecting-IP和X-Forwarded-For头部，HttpServletRequest获取到的IP为：{}", request.getRemoteAddr()); }
        filterChain.doFilter(request, response); } }