package xyz.thewhitedog9487.WebAPI.Configuration.Security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.getBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import xyz.thewhitedog9487.WebAPI.BackgroundTask.ApiKeyManager
import xyz.thewhitedog9487.WebAPI.Controller.ResponseData
import xyz.thewhitedog9487.WebAPI.Data.Entity.AccessLog
import xyz.thewhitedog9487.WebAPI.Miscellaneous.SpringContextHolder.SpringContext
import java.nio.charset.StandardCharsets

class ApiKeyAuthenticationFilter: OncePerRequestFilter() {
    val ApiKeyManagerInstance = SpringContext.getBean<ApiKeyManager>()

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val ApiKey = request.getHeader("X-API-Key") ?: run {
            val Log = AccessLog(request)
            Log.ResponseStatusCode = HttpStatus.UNAUTHORIZED.value()
            Log.SaveIntoDatabase()
            val ResponseBody = ResponseData(
                HttpStatus.UNAUTHORIZED.value(),
                "API密钥验证失败，未传递X-API-Key请求头")
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = StandardCharsets.UTF_8.name()
            response.status = HttpStatus.UNAUTHORIZED.value()
            response.writer.write(ResponseBody.json)
            return }
        if (ApiKey in ApiKeyManagerInstance.ApiKeyList) {
            val Auth = UsernamePasswordAuthenticationToken(
                "",
                ApiKey,
                listOf(SimpleGrantedAuthority("API")))
            SecurityContextHolder.getContext().authentication = Auth
            filterChain.doFilter(request, response) }
        else {
            val Log = AccessLog(request)
            Log.ResponseStatusCode = HttpStatus.FORBIDDEN.value()
            Log.SaveIntoDatabase()
            val ResponseBody = ResponseData(
                HttpStatus.FORBIDDEN.value(),
                "API密钥验证失败，API密钥无效")
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = StandardCharsets.UTF_8.name()
            response.status = HttpStatus.FORBIDDEN.value()
            response.writer.write(ResponseBody.json)
            return } } }