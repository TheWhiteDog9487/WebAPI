package xyz.thewhitedog9487.WebAPI;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Component
class GlobalSharedBean {

    @Bean
    GatewayDiscordClient GetDiscordClient(){
        String DiscordBotToken = System.getenv("Discord_Bot_Token");
        if (DiscordBotToken == null) {
            log.error("未设置环境变量Discord_Bot_Token，请检查配置。");
            System.exit(-1); }
        return DiscordClientBuilder.create(DiscordBotToken)
                .build()
                .login()
                .block();}
        // TODO: ↑ 看看能不能优化下启动性能

    @Bean
    List<String> ApiKeyList() {
        Path FileName = Path.of("API密钥.txt");
        try {
            return Files.readAllLines(FileName, StandardCharsets.UTF_8);
        } catch (IOException e) {
            try {
                Files.createFile(FileName);
                var DefaultPassword = RandomStringUtils.secure().nextAlphanumeric(30);
                Files.writeString(FileName, DefaultPassword, StandardCharsets.UTF_8);
                log.info("已生成API密钥文件，默认密钥为：{}", DefaultPassword);
                return List.of(DefaultPassword);
            } catch (IOException e1) {
                log.error("API密钥文件不存在且无法创建API密钥文件。", e1);
                System.exit(-1); }
            return null; } }
}
