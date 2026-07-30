package xyz.thewhitedog9487.WebAPI.Miscellaneous

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
object SpringContextHolder: ApplicationContextAware{
    lateinit var SpringContext: ApplicationContext

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        SpringContext = applicationContext } }