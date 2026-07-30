package xyz.thewhitedog9487.WebAPI.Controller

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.view.RedirectView

@Controller
class RedirectRootToSwaggerWebUI {
    val Logger: KLogger = KotlinLogging.logger{}
    val RedirectTo = "/swagger-ui/index.html"

    @GetMapping("/")
    fun Redirect(): RedirectView {
        Logger.info{"正在将访问 / 的请求重定向到 $RedirectTo"}
        return RedirectView(RedirectTo) } }