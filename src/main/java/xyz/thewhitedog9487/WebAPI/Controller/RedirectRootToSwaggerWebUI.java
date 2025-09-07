package xyz.thewhitedog9487.WebAPI.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@Controller
class RedirectRootToSwaggerWebUI {
    static String RedirectTo = "/swagger-ui/index.html";

    @GetMapping("/")
    RedirectView Redirect(){
        log.info("正在将访问 / 的请求重定向到 {}", RedirectTo);
        return new RedirectView(RedirectTo); } }