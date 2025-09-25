package xyz.thewhitedog9487.WebAPI.Startup;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.system.ApplicationHome;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class SystemdInstallerManager{
    static String TemplateContent = """
        [Unit]
        Description=WebAPI
        After=network.target

        [Service]
        User=%s
        WorkingDirectory=%s
        ExecStart=%s
        Restart=always
        Type=simple
        Environment="%s"

        [Install]
        WantedBy=multi-user.target
        """;

    static ApplicationHome AppHome = new ApplicationHome( SystemdInstallerManager.class );
    static String CurrentOS = System.getProperty("os.name");
    static String CurrentUser = System.getProperty("user.name");
    static Path CurrentExecutableFilePath = GetExecutblePath();
    static Path WorkingDirectory = AppHome
            .getDir()
            .toPath();
    static Path ServiceFileDirectory = Path.of("/etc/systemd/system/");
    static Path ServiceFileName = Path.of("WebAPI.service");
    static String SoftLinkFileName = "Current";
    static Path SymbolicLinkPath = WorkingDirectory.resolve(SoftLinkFileName);

    /**
     * 检测当前运行环境是否为GraalVM生成的Native Image
     */
    static boolean IsImageCode(){
        try {
            return (boolean) Class.forName("org.graalvm.nativeimage.ImageInfo")
                    .getMethod("inImageCode")
                    .invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            return false; } }

    /**
     * 获取当前可执行文件的路径。
     * <br>
     * 如果是通过GraalVM生成的Native Image，则返回可执行文件的路径
     * <br>
     * 如果是直接用JRE运行的JAR包，则返回JAR文件的路径
     */
    static Path GetExecutblePath(){
        try {
            var IsImageCode = IsImageCode();
            log.debug("IsImageCode: {}", IsImageCode);
            if( IsImageCode == true ){
                Object ExecutablePath = Class.forName("org.graalvm.nativeimage.ProcessProperties")
                        .getMethod("getExecutableName")
                        .invoke(null);
                log.debug("ExecutablePath: {}", ExecutablePath);
                return Path.of((String) ExecutablePath); }
            else{
                // ↓ 没有错误，但是获取Jar包位置的代码在catch里面，扔一个异常出去以进入catch分支
                // ↓ 这个主要是针对使用GraalVM的JVM而不是Native Image的情况
                throw new RuntimeException("Not Image Code"); }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException |
                 RuntimeException e) {
            try {
                return AppHome
                        .getSource()
                        .toPath()
                        .toRealPath();
            } catch (IOException ex) {
                throw new RuntimeException(ex); } } }

    @SneakyThrows
    public static void ProcessArguments(String[] CommandLineArguments) {
        log.debug("CommandLineArguments: {}", Arrays.toString(CommandLineArguments));
        for (String Argument : CommandLineArguments) {
            if ( List.of("install", "--install").contains(Argument) == true ) {
                /*
                在Native Image / Jar文件旁边生成指向自身的，名为Current的软连接
                然后在软连接旁生成systemd服务文件，执行目标指向软连接
                */
                List<String> DiscordBotToken = Arrays.stream(CommandLineArguments)
                        .filter(s -> {
                            for (String o : List.of("--Discord_Bot_Token=")) {
                                if (s.startsWith(o) == true) { return true; } }
                            return false; } )
                        .toList();
                if ( DiscordBotToken.isEmpty() == true ) {
                    log.error("必须通过\"--Discord_Bot_Token=\"参数提供Discord机器人的令牌以使本程序正常工作。");
                    throw new IllegalArgumentException("缺少必须的--Discord_Bot_Token参数"); }
                log.info("开始安装systemd服务");
                log.debug("SystemProperties.os.name: {}", CurrentOS);
                if ( CurrentOS.startsWith("Linux") ) {
                    log.debug("SystemProperties.user.name: {}", CurrentUser);
                    if (CurrentUser.equals("root") == false) {
                        log.error("只有root才有权在系统层级安装systemd服务，但是JVM报告的当前用户是{}", CurrentUser);
                        throw new IllegalStateException("请使用root用户运行此命令以安装为systemd服务"); }
                    log.debug("CurrentExecutableFilePath: {}", CurrentExecutableFilePath);
                    log.debug("WorkingDirectory: {}", WorkingDirectory);
                    log.debug("SymbolicLinkPath: {}", SymbolicLinkPath);
                    new ProcessBuilder("systemctl", "stop", ServiceFileName.toString()).start().waitFor();
                    log.info("已停止systemd服务: {}", ServiceFileName);
                    Files.deleteIfExists(SymbolicLinkPath);
                    Files.createSymbolicLink(SymbolicLinkPath, CurrentExecutableFilePath);
                    log.info("成功创建指向当前程序的软链接: {} -> {}", SymbolicLinkPath, CurrentExecutableFilePath);
                    Path CurrentJavaPath = null;
                    var IsImageCode = IsImageCode();
                    if ( IsImageCode == false ) {
                        CurrentJavaPath = Path.of("/proc/self/exe").toRealPath();
                        log.debug("CurrentJavaPath: {}", CurrentJavaPath); }
                    var ExecCommand = (IsImageCode == true) ?
                            SymbolicLinkPath.toString() :
                            CurrentJavaPath + " -jar " + SymbolicLinkPath.toString();
                    log.debug("ExecCommand: {}", ExecCommand);
                    var ServiceFileContent = String.format(
                            TemplateContent,
                            CurrentUser,
                            WorkingDirectory,
                            ExecCommand,
                            DiscordBotToken
                                    .getFirst()
                                    .substring("--"
                                            .length()));
                    log.debug("ServiceFileContent: \n{}", ServiceFileContent);
                    Files.deleteIfExists( WorkingDirectory.resolve(ServiceFileName) );
                    Files.writeString(WorkingDirectory.resolve(ServiceFileName), ServiceFileContent);
                    log.info("成功创建systemd服务文件: {}", WorkingDirectory.resolve(ServiceFileName));
                    Files.deleteIfExists( ServiceFileDirectory.resolve(ServiceFileName) );
                    Files.createSymbolicLink(ServiceFileDirectory.resolve(ServiceFileName), WorkingDirectory.resolve(ServiceFileName));
                    log.info("成功创建指向服务文件的软链接: {} -> {}", ServiceFileDirectory.resolve(ServiceFileName), WorkingDirectory.resolve(ServiceFileName));
                    new ProcessBuilder("systemctl", "daemon-reload").start().waitFor();
                    log.info("已完成systemd守护进程重载");
                    new ProcessBuilder("systemctl", "enable", ServiceFileName.toString()).start().waitFor();
                    log.info("已启用systemd服务: {}", ServiceFileName);
                    new ProcessBuilder("systemctl", "start", ServiceFileName.toString()).start().waitFor();
                    log.info("已启动systemd服务: {}", ServiceFileName); }
                else if ( CurrentOS.startsWith("Windows") ) {
                    log.error("只有Linux才能使用systemd，但是JVM报告的当前系统是{}", CurrentOS);
                    throw new IllegalStateException("Windows系统不支持systemd服务管理，暂不支持在Windows上自动管理服务。"); }
                System.exit(0); }
                else if ( List.of("uninstall", "--uninstall").contains(Argument) == true ) {
                    log.info("开始卸载systemd服务");
                    log.debug("SystemProperties.os.name: {}", CurrentOS);
                    if ( CurrentOS.startsWith("Windows") ) {
                        log.error("只有Linux才能使用systemd，但是JVM报告的当前系统是{}", CurrentOS);
                        throw new IllegalStateException("Windows系统不支持systemd服务管理，暂不支持在Windows上自动管理服务。"); }
                    else if ( CurrentOS.startsWith("Linux") ) {
                        if (CurrentUser.equals("root") == false) {
                            log.error("只有root才有权在系统层级卸载systemd服务，但是JVM报告的当前用户是{}", CurrentUser);
                            throw new IllegalStateException("请使用root用户运行此命令以卸载systemd服务"); }
                        new ProcessBuilder("systemctl", "stop", ServiceFileName.toString()).start().waitFor();
                        log.info("已停止systemd服务: {}", ServiceFileName);
                        new ProcessBuilder("systemctl", "disable", ServiceFileName.toString()).start().waitFor();
                        log.info("已禁用systemd服务: {}", ServiceFileName);
                        Files.deleteIfExists(ServiceFileDirectory.resolve(ServiceFileName));
                        log.info("成功删除systemd服务文件: {}", ServiceFileDirectory.resolve(ServiceFileName));
                        Files.deleteIfExists(WorkingDirectory.resolve(ServiceFileName));
                        log.info("成功删除程序旁的服务文件: {}", WorkingDirectory.resolve(ServiceFileName));
                        Files.deleteIfExists(SymbolicLinkPath);
                        log.info("成功删除指向程序自身的软链接: {}", SymbolicLinkPath);
                        new ProcessBuilder("systemctl", "daemon-reload").start().waitFor();
                        log.info("已完成systemd守护进程重载"); }
                    System.exit(0); } } }
}
