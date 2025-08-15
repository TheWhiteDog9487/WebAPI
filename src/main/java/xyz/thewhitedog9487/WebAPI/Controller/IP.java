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
        if ( HttpHeader.get("CF-Connecting-IP".toLowerCase() ) instanceof String IP) {
            /*
            ↑ 如果成功完成instanceof模式匹配，那么模式变量一定非空
            相当于是：
            if ( HttpHeader.get("CF-Connecting-IP".toLowerCase() ) != null ) {
                String IP = HttpHeader.get( "CF-Connecting-IP".toLowerCase() ); }
            */
            return new ResponseEntity<>(IP,
                    MultiValueMap.fromSingleValue(Map.of("Content-Type", "text/plain;charset=UTF-8")),
                    HttpStatus.OK); }
        else if (HttpHeader.get("X-Forwarded-For".toLowerCase()) instanceof String IP) {
                IP = IP.split(",")[0].trim();
                return new ResponseEntity<>(IP,
                        MultiValueMap.fromSingleValue(Map.of("Content-Type", "text/plain;charset=UTF-8")),
                        HttpStatus.OK); }
        else {
            return new ResponseEntity<>(Request.getRemoteAddr(),
                    MultiValueMap.fromSingleValue(Map.of("Content-Type", "text/plain;charset=UTF-8")),
                    HttpStatus.OK); } }

    @GetMapping("iso3166")
    ResponseEntity<String> GetISO3166(@RequestHeader Map<String, String> HttpHeader) {
        if (HttpHeader.get("CF-IPCountry".toLowerCase()) instanceof String CountryCode) {
            return new ResponseEntity<>(CountryCode,
                    MultiValueMap.fromSingleValue(Map.of("Content-Type", "text/plain;charset=UTF-8")),
                    HttpStatus.OK); }
        else {
            log.warn("请求尝试获取其ip对应的iso3166代码，但是我们没有找到CF-IPCountry头部");
            return new ResponseEntity<>("未找到CF-IPCountry头部",
                    MultiValueMap.fromSingleValue(Map.of("Content-Type", "text/plain;charset=UTF-8")),
                    HttpStatus.NOT_FOUND); } }
}
