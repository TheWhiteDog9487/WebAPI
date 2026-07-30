package xyz.thewhitedog9487.WebAPI.Controller.Filter

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.util.ContentCachingResponseWrapper
import xyz.thewhitedog9487.WebAPI.Data.Entity.AccessLog

@Component
class LogClientInfo(): Filter {
    val Logger: KLogger = KotlinLogging.logger{}

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val HttpServletRequest = request as HttpServletRequest
        val HttpServletResponse = response as HttpServletResponse
        val CachedResponse = ContentCachingResponseWrapper(HttpServletResponse)
        val Log = AccessLog(HttpServletRequest )
        try {
            chain.doFilter(request, CachedResponse)
            if (request.isAsyncStarted){
                request.asyncContext.addListener(object : AsyncListener {
                    override fun onComplete(event: AsyncEvent?) {
                        Log.ResponseStatusCode = CachedResponse.status
                        Log.SaveIntoDatabase(CachedResponse) }
                    override fun onTimeout(event: AsyncEvent?) {
                        Logger.error{ event?.throwable?.localizedMessage }
                        Log.ResponseStatusCode = CachedResponse.status
                        Log.SaveIntoDatabase(CachedResponse) }
                    override fun onError(event: AsyncEvent?) = onTimeout(event)
                    override fun onStartAsync(event: AsyncEvent?) { } } )
                return }
            Log.ResponseStatusCode = CachedResponse.status
        } catch (e: Exception) {
            Logger.error{ e.localizedMessage }
            Log.ResponseStatusCode = CachedResponse.status
            Log.SaveIntoDatabase(CachedResponse)
            throw e }
        Log.SaveIntoDatabase(CachedResponse) } }
