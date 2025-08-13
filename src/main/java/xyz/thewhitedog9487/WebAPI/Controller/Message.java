package xyz.thewhitedog9487.WebAPI.Controller;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.rest.http.client.ClientException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/message")
class Message {
    @Autowired GatewayDiscordClient DiscordBotClient;
    @Autowired List<String> ApiKeyList;
    record PostMessageData(Long ChannelID, String Content) {}

    @PostMapping("/discord")
    ResponseEntity<ResponseData> DiscordPush(@RequestHeader(value = "X-API-Key", required = true) String ApiKey, @RequestBody PostMessageData RequestBody){
        if ( ApiKeyList.contains(ApiKey) == false ) {
            log.warn("API密钥验证失败，密钥：{}", ApiKey);
            return new ResponseEntity<>(new ResponseData(
                    HttpStatus.UNAUTHORIZED.value(),
                    "API密钥验证失败",
                    Map.of("提供的密钥", ApiKey)), HttpStatus.UNAUTHORIZED); }

        var ChannelID = Snowflake.of(RequestBody.ChannelID);
        log.info("准备向{}发送消息: {}", ChannelID.asString(), RequestBody.Content);
        try {
            var MessageData = DiscordBotClient
                    .rest()
                    .getChannelById(ChannelID)
                    .createMessage(RequestBody.Content)
                    .block();
            log.info("消息发送成功，ID为：{}", MessageData.id());
            return new ResponseEntity<>(new ResponseData(
                    HttpStatus.CREATED.value(),
                    "消息发送成功",
                    Map.of(
                            "新ID", MessageData.id().asString(),
                            "频道ID", ChannelID.asString(),
                            "内容", RequestBody.Content)), HttpStatus.CREATED);
        } catch (ClientException e) {
            log.error("消息发送失败：", e);
            return new ResponseEntity<>(new ResponseData(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "消息发送失败",
                    Map.of(
                            "错误信息", e.getMessage(),
                            "频道ID", ChannelID.asString(),
                            "内容", RequestBody.Content)), HttpStatus.INTERNAL_SERVER_ERROR);}}

    @ExceptionHandler(MissingRequestHeaderException.class)
    ResponseEntity<ResponseData> HandleMissingHeader(MissingRequestHeaderException Exception, HttpServletRequest Request) {
        log.warn("请求缺少必要的头部信息：{}", Exception.getHeaderName());
        return new ResponseEntity<>(new ResponseData(
                HttpStatus.BAD_REQUEST.value(),
                "请求缺少必要的头部信息",
                Map.of("缺失的头部", Exception.getHeaderName())), HttpStatus.BAD_REQUEST);}
}
