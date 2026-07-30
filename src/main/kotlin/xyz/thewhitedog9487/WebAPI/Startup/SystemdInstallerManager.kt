package xyz.thewhitedog9487.WebAPI.Startup

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.system.ApplicationHome
import xyz.thewhitedog9487.WebAPI.WebApiApplication
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.system.exitProcess

val Logger = KotlinLogging.logger{}
val AppHome: ApplicationHome = ApplicationHome(WebApiApplication::class.java)
val CurrentOS: String = System.getProperty("os.name")
val CurrentUser: String = System.getProperty("user.name")
/**
 * 当前可执行文件的路径。
 *
 * 如果是通过GraalVM生成的Native Image，则是可执行文件的路径
 *
 * 如果是直接用JRE运行的JAR包，则是JAR文件的路径
 */
val CurrentExecutableFilePath: Path get() {
    Logger.debug { "IsImageCode: $IsImageCode" }
    if (IsImageCode == true) {
        val ExecutablePath = Class.forName("org.graalvm.nativeimage.ProcessProperties")
            .getMethod("getExecutableName")
            .invoke(null) as String
        Logger.debug { "${"ExecutablePath: {}"} $ExecutablePath" }
        return Path(ExecutablePath)
    } else {
//           ↓ 使用GraalVM的JVM而不是Native Image
        return AppHome
            .source!!
            .toPath()
            .toRealPath() } }
val WorkingDirectory: Path = AppHome
    .dir
    .toPath()
val ServiceFileDirectory: Path = Path("/etc/systemd/system/")
val ServiceFileName: Path = Path("WebAPI.service")
const val SoftLinkFileName: String = "Current"
val SymbolicLinkPath: Path = WorkingDirectory.resolve(SoftLinkFileName)
var DiscordBotToken: String = System.getenv("Discord_Bot_Token")

/**
 * 检测当前运行环境是否为GraalVM生成的Native Image
 */
val IsImageCode: Boolean get() {
    return try {
        Class.forName("org.graalvm.nativeimage.ImageInfo")
            .getMethod("inImageCode")
            .invoke(null) as Boolean
    } catch (_: Exception) {
        false } }

fun ProcessArguments(CommandLineArguments: Array<String>) {
    Logger.debug { "${"CommandLineArguments: {}"} ${CommandLineArguments.contentToString()}" }
    for (Argument in CommandLineArguments) {
        if (Argument in listOf("install", "--install")) {
            /*
            在Native Image / Jar文件旁边生成指向自身的，名为Current的软连接
            然后在软连接旁生成systemd服务文件，执行目标指向软连接
            */
            DiscordBotToken = if (CommandLineArguments.contains("--Discord_Bot_Token") &&
                CommandLineArguments.getOrNull(CommandLineArguments.indexOf("--Discord_Bot_Token") + 1) != null ){
                CommandLineArguments[CommandLineArguments.indexOf("--Discord_Bot_Token") + 1] }
                            else System.getProperty("Discord_Bot_Token", "")
            if (DiscordBotToken.isEmpty()) {
                Logger.error { "必须通过--Discord_Bot_Token参数或Discord_Bot_Token环境变量提供Discord机器人的令牌以使本程序正常工作。" }
                throw IllegalArgumentException("缺少必须的--Discord_Bot_Token参数") }
            Logger.info { "开始安装systemd服务" }
            Logger.debug { "SystemProperties.os.name: $CurrentOS" }
            if (CurrentOS.startsWith("Linux")) {
                Logger.debug { "SystemProperties.user.name: $CurrentUser" }
                if (CurrentUser != "root") {
                    Logger.error { "只有root才有权在系统层级安装systemd服务，但是JVM报告的当前用户是$CurrentUser" }
                    throw IllegalStateException("请使用root用户运行此命令以安装为systemd服务") }
                Logger.debug { "CurrentExecutableFilePath: $CurrentExecutableFilePath" }
                Logger.debug { "WorkingDirectory: $WorkingDirectory" }
                Logger.debug { "SymbolicLinkPath: $SymbolicLinkPath" }
                ProcessBuilder("systemctl", "stop", ServiceFileName.toString()).start().waitFor()
                Logger.info { "已停止systemd服务: $ServiceFileName" }
                Files.deleteIfExists(SymbolicLinkPath)
                Files.createSymbolicLink(SymbolicLinkPath, CurrentExecutableFilePath)
                Logger.info { "成功创建指向当前程序的软链接: $SymbolicLinkPath -> $CurrentExecutableFilePath" }
                val CurrentJavaPath by lazy {
                    if (IsImageCode == false) {
                        Logger.debug { "CurrentJavaPath: ${Path("/proc/self/exe").toRealPath()}" }
                        Path("/proc/self/exe").toRealPath() }
                    else throw Exception() }
                val ExecCommand = if (IsImageCode) SymbolicLinkPath.toString() else "$CurrentJavaPath -jar $SymbolicLinkPath"
                Logger.debug { "ExecCommand: $ExecCommand" }
                val ServiceFileContent = """
                    [Unit]
                    Description=WebAPI
                    After=network.target
                
                    [Service]
                    User=$CurrentUser
                    WorkingDirectory=$WorkingDirectory
                    ExecStart=$ExecCommand
                    Restart=always
                    Type=simple
                    Environment="Discord_Bot_Token=$DiscordBotToken"
                
                    [Install]
                    WantedBy=multi-user.target
                    
                    """.trimIndent()
                Logger.debug { "ServiceFileContent: \n$ServiceFileContent" }
                Files.deleteIfExists(WorkingDirectory.resolve(ServiceFileName))
                Files.writeString(WorkingDirectory.resolve(ServiceFileName), ServiceFileContent)
                Logger.info { "成功创建systemd服务文件: ${WorkingDirectory.resolve(ServiceFileName)}" }
                Files.deleteIfExists(ServiceFileDirectory.resolve(ServiceFileName))
                Files.createSymbolicLink(
                    ServiceFileDirectory.resolve(ServiceFileName),
                    WorkingDirectory.resolve(ServiceFileName) )
                Logger.info {
                    "成功创建指向服务文件的软链接: ${ServiceFileDirectory.resolve(ServiceFileName)} -> ${WorkingDirectory.resolve(ServiceFileName)}" }
                ProcessBuilder("systemctl", "daemon-reload").start().waitFor()
                Logger.info { "已完成systemd守护进程重载" }
                ProcessBuilder("systemctl", "enable", ServiceFileName.toString()).start().waitFor()
                Logger.info { "已启用systemd服务: $ServiceFileName" }
                ProcessBuilder("systemctl", "start", ServiceFileName.toString()).start().waitFor()
                Logger.info { "已启动systemd服务: $ServiceFileName" }
            } else if (CurrentOS.startsWith("Windows")) {
                Logger.error{"只有Linux才能使用systemd，但是JVM报告的当前系统是$CurrentOS"}
                throw IllegalStateException("Windows系统不支持systemd服务管理，暂不支持在Windows上自动管理服务。") }
            exitProcess(0)
        } else if (Argument in listOf("uninstall", "--uninstall")) {
            Logger.info { "开始卸载systemd服务" }
            Logger.debug { "SystemProperties.os.name: $CurrentOS" }
            if (CurrentOS.startsWith("Windows")) {
                Logger.error { "只有Linux才能使用systemd，但是JVM报告的当前系统是$CurrentOS" }
                throw IllegalStateException("Windows系统不支持systemd服务管理，暂不支持在Windows上自动管理服务。")
            } else if (CurrentOS.startsWith("Linux")) {
                if (CurrentUser != "root") {
                    Logger.error { "只有root才有权在系统层级卸载systemd服务，但是JVM报告的当前用户是$CurrentUser" }
                    throw IllegalStateException("请使用root用户运行此命令以卸载systemd服务") }
                ProcessBuilder("systemctl", "stop", ServiceFileName.toString()).start().waitFor()
                Logger.info { "已停止systemd服务: $ServiceFileName" }
                ProcessBuilder("systemctl", "disable", ServiceFileName.toString()).start().waitFor()
                Logger.info { "已禁用systemd服务: $ServiceFileName" }
                Files.deleteIfExists(ServiceFileDirectory.resolve(ServiceFileName))
                Logger.info { "成功删除systemd服务文件: ${ServiceFileDirectory.resolve(ServiceFileName)}" }
                Files.deleteIfExists(WorkingDirectory.resolve(ServiceFileName))
                Logger.info { "成功删除程序旁的服务文件: ${WorkingDirectory.resolve(ServiceFileName)}" }
                Files.deleteIfExists(SymbolicLinkPath)
                Logger.info { "成功删除指向程序自身的软链接: $SymbolicLinkPath" }
                ProcessBuilder("systemctl", "daemon-reload").start().waitFor()
                Logger.info { "已完成systemd守护进程重载" } }
            exitProcess(0) } } }