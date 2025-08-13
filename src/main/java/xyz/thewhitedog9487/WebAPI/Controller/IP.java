package xyz.thewhitedog9487.WebAPI.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/ip")
class IP {
    @GetMapping("/ip")
    ResponseEntity<String> GetIP(@RequestHeader Map<String, String> HttpHeader, HttpServletRequest Request) {
        if (HttpHeader.get("CF-Connecting-IP".toLowerCase()) instanceof String IP) {
            /*
            ↑ instanceof的模式变量一定非空
            相当于是：
            if (HttpHeader.get("CF-Connecting-IP" != null) {
                String IP = HttpHeader.get("CF-Connecting-IP");
            }
            */
            log.info("请求携带了CF-Connecting-IP头部，IP为：{}", IP);
            return new ResponseEntity<>(IP,
                    MultiValueMap.fromSingleValue(Map.of("Content-Type", "text/plain;charset=UTF-8")),
                    HttpStatus.OK); }
        else {
            if (HttpHeader.get("X-Forwarded-For".toLowerCase()) instanceof String IP) {
                IP = IP.split(",")[0].trim();
                log.info("请求携带了X-Forwarded-For头部，IP为：{}", IP);
                return new ResponseEntity<>(IP,
                        MultiValueMap.fromSingleValue(Map.of("Content-Type", "text/plain;charset=UTF-8")),
                        HttpStatus.OK); }
            log.info("请求未携带CF-Connecting-IP和X-Forwarded-For头部，HttpServletRequest获取到的IP为：{}", Request.getRemoteAddr());
            return new ResponseEntity<>(Request.getRemoteAddr(),
                    MultiValueMap.fromSingleValue(Map.of("Content-Type", "text/plain;charset=UTF-8")),
                    HttpStatus.OK); }
    }
}
