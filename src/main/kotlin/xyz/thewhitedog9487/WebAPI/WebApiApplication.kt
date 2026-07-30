package xyz.thewhitedog9487.WebAPI

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.core.context.SecurityContextHolder
import reactor.core.publisher.Hooks
import xyz.thewhitedog9487.WebAPI.Startup.ProcessArguments

@SpringBootApplication
class WebApiApplication

fun main(args: Array<String>) {
	Hooks.enableAutomaticContextPropagation()
	SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL)
	ProcessArguments(args)
	runApplication<WebApiApplication>(*args)
}