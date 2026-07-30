package xyz.thewhitedog9487.WebAPI.Miscellaneous

import dev.kord.core.Kord
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.SmartLifecycle
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.util.concurrent.Executors
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

val VirtualThreadCoroutineDispatcher: ExecutorCoroutineDispatcher =
    Executors.newVirtualThreadPerTaskExecutor()
        .asCoroutineDispatcher()

@Component
class GlobalSharedBean {
    val Logger = KotlinLogging.logger{}

    @Bean
    fun DiscordClient(@Value("\${Discord_Bot_Token:}") DiscordBotToken: String): Kord {
        if (DiscordBotToken.isEmpty()) {
            Logger.error { "未通过参数--Discord_Bot_Token或环境变量Discord_Bot_Token提供Discord登录令牌，请检查配置。" }
            throw IllegalArgumentException("未提供Discord登录令牌") }
        return runBlocking(VirtualThreadCoroutineDispatcher) { Kord(DiscordBotToken) } }

    @Bean
    fun KordLifecycleManager(KordInstance: Kord) = object: SmartLifecycle {
        var Job: Job? = null
        override fun start() {
            Job = CoroutineScope(VirtualThreadCoroutineDispatcher).launch {
                KordInstance.login { } } }

        override fun stop() {
            runBlocking(VirtualThreadCoroutineDispatcher) {
                try {
                    KordInstance.logout()
                } catch (_: IllegalStateException) { }
                Job?.cancelAndJoin() } }

        override fun isRunning() = Job?.isActive ?: false }

    @get:Bean
    val SQLiteWriteLock: Lock = ReentrantLock() }