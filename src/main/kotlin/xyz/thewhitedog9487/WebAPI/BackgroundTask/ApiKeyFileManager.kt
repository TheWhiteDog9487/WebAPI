package xyz.thewhitedog9487.WebAPI.BackgroundTask

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.stereotype.Component
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.io.path.absolute
import kotlin.system.exitProcess

@Component
class ApiKeyManager {
    private val Logger: KLogger = KotlinLogging.logger{}
    internal val ApiKeyFile = Path.of("API密钥.txt").absolute()

    internal val ApiKeyListBackendField: CopyOnWriteArraySet<String> = CopyOnWriteArraySet()
    val ApiKeyList: Set<String> get() = ApiKeyListBackendField

    init {
        try {
            if (Files.exists(ApiKeyFile)) {
                ApiKeyListBackendField.clear()
                ApiKeyListBackendField.addAll(Files.readAllLines(ApiKeyFile, StandardCharsets.UTF_8))
                Logger.info{"API密钥列表已完成加载，当前数量：${ApiKeyListBackendField.size}"}
            } else {
                try {
                    Files.createFile(ApiKeyFile)
                    val DefaultPassword = RandomStringUtils.secure().nextAlphanumeric(32)
                    Files.writeString(ApiKeyFile, DefaultPassword, StandardCharsets.UTF_8)
                    Logger.info{"已生成API密钥文件，默认密钥为：$DefaultPassword"}
                    ApiKeyListBackendField.clear()
                    ApiKeyListBackendField.add(DefaultPassword)
                } catch (Exception: IOException) {
                    Logger.error{"API密钥文件不存在且无法创建API密钥文件。"}
                    Logger.error { Exception }
                    exitProcess(-1) } }
        } catch (Exception: IOException) {
            Logger.error{"读取API密钥文件失败 $Exception"} }

        Thread.startVirtualThread {
            val ParentDirectory = ApiKeyFile.parent
            val WatchService = FileSystems.getDefault().newWatchService()
            ParentDirectory.register(WatchService, StandardWatchEventKinds.ENTRY_MODIFY)
            Logger.info { "开始在目录 $ParentDirectory 监视API密钥文件变动：${ApiKeyFile.fileName}" }
            while (true) {
                val WatchKey = WatchService.take()
                for (Event in WatchKey.pollEvents()) {
                    val ChangedFile = Event.context() as Path
                    if (ChangedFile.toString() == ApiKeyFile.fileName.toString()) {
                        Logger.info { "检测到API密钥文件 ${Event.kind()} 事件，正在重载..." }
                        try {
                            ApiKeyListBackendField.clear()
                            ApiKeyListBackendField.addAll(Files.readAllLines(ApiKeyFile, StandardCharsets.UTF_8))
                            Logger.info { "API密钥列表已重新加载，当前数量：${ApiKeyListBackendField.size}" }
                        } catch (Exception: IOException) {
                            Logger.error { "读取API密钥文件失败 $Exception" } } } }
                WatchKey.reset() } } } }