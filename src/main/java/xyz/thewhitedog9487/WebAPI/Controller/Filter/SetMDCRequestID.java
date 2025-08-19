package xyz.thewhitedog9487.WebAPI.Controller.Filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
// ↑ 拉高优先级，确保在日志记录之前设置MDC，不然和没设置就没有区别了
class SetMDCRequestID extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            MDC.put("RequestID", request.getRequestId());
            filterChain.doFilter(request, response);
        } finally {
            // ↓ 防止内存泄漏
            MDC.remove("RequestID"); } } }