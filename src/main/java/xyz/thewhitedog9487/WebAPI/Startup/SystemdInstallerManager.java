package xyz.thewhitedog9487.WebAPI.Startup;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Component
class SystemdInstallerManager implements CommandLineRunner {
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

        [Install]
        WantedBy=multi-user.target
        """;

    String CurrentOS = System.getProperty("os.name");
    String CurrentUser = System.getProperty("user.name");
    Path CurrentExecutableFilePath = GetExecutblePath();
    Path WorkingDirectory = CurrentExecutableFilePath.getParent();
    Path ServiceFileDirectory = Path.of("/etc/systemd/system/");
    Path ServiceFileName = Path.of("WebAPI.service");
    String SoftLinkFileName = "Current";
    Path SymbolicLinkPath = WorkingDirectory.resolve(SoftLinkFileName);

    /**
     * 检测当前运行环境是否为GraalVM生成的Native Image
     */
    boolean IsImageCode(){
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
    Path GetExecutblePath(){
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
                log.debug("IsImageCode is false");
                // ↓ 没有错误，但是获取Jar包位置的代码在catch里面，扔一个异常出去以进入catch分支
                // ↓ 这个主要是针对使用GraalVM的JVM而不是Native Image的情况
                throw new RuntimeException("Not Image Code"); }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException |
                 RuntimeException e) {
            try {
                return Path.of(SystemdInstallerManager.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toURI()
                        .getPath());
            } catch (URISyntaxException ex) {
                throw new RuntimeException(ex);}}}

    @Override
    public void run(String... args) throws Exception {
        for (String Argument : args) {
            if ( List.of("install", "--install").contains(Argument) == true ) {
                    /*
                    在Native Image / Jar文件旁边生成指向自身的，名为Current的软连接
                    然后在软连接旁生成systemd服务文件，执行目标指向软连接
                     */
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
                                ExecCommand);
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
                System.exit(0);
            }
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
                    System.exit(0); } } } }