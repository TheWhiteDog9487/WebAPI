package xyz.thewhitedog9487.WebAPI;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import lombok.extern.slf4j.Slf4j;
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
        try {
            return Files.readAllLines(Path.of("API密钥.txt"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            try {
                Files.createFile(Path.of("API密钥.txt"));
            } catch (IOException e1) {
                log.error("API密钥文件不存在且无法创建API密钥文件。", e1);
                System.exit(-1); }
            log.error("读取API密钥文件不存在，已生成空文件，请放置密钥。", e);
            System.exit(-1);
            return null; } }
}
