package xyz.thewhitedog9487.WebAPI.Controller;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.rest.http.client.ClientException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name="远程消息处理相关")
@Slf4j
@RestController
@RequestMapping("/message")
class Message {
    @Autowired GatewayDiscordClient DiscordBotClient;
    @Autowired List<String> ApiKeyList;

    @Schema(description = "包含了发送消息的数据包")
    record PostMessageData(
            @Schema(description = "Discord频道ID", example = "1398192763845214239") Long ChannelID,
            @Schema(description = "要发送的消息内容", example = "成功完成备份，最新文件时间为2025 08 21") String Content) {}

    @Operation(summary = "通过Discord Bot向指定频道发送消息", description = """
            这是一个私有API，需要在请求头中提供X-API-Key以进行身份验证
            <br>
            需要在请求体中提供频道ID和消息内容
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ResponseData.class),
                        examples = @ExampleObject(value = "{\n" +
                                "  \"code\": 201,\n" +
                                "  \"message\": \"消息发送成功\",\n" +
                                "  \"data\": {\n" +
                                "    \"新ID\": \"1407848258813825135\",\n" +
                                "    \"内容\": \"写点什么好呢\",\n" +
                                "    \"频道ID\": \"1398192763845214239\"\n" +
                                "  }\n" +
                                "}") ),
                    description = "消息发送成功，响应体中包含新消息的ID、频道ID和内容"),
            @ApiResponse(responseCode = "400",
                    content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ResponseData.class),
                        examples = @ExampleObject(value = "{\n" +
                                "  \"code\": 400,\n" +
                                "  \"message\": \"请求缺少必要的头部信息\",\n" +
                                "  \"data\": {\n" +
                                "    \"缺失的头部\": \"X-API-Key\"\n" +
                                "  }\n" +
                                "}") ),
                    description = "缺少必须的请求头，请查看响应中的“缺失的头部”以诊断问题"),
            @ApiResponse(responseCode = "401",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseData.class),
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"code\": 401,\n" +
                                    "  \"message\": \"API密钥验证失败\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"提供的密钥\": \"123456\"\n" +
                                    "  }\n" +
                                    "}") ),
                    description = "API密钥验证失败"),
            @ApiResponse(responseCode = "500",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseData.class),
                            examples = @ExampleObject(value = "{\n" +
                                    "  \"code\": 500,\n" +
                                    "  \"message\": \"消息发送失败\",\n" +
                                    "  \"data\": {\n" +
                                    "    \"内容\": \"芝士异常\",\n" +
                                    "    \"错误信息\": \"POST /channels/1398192763845214/messages returned 404 Not Found with response {code=10003, message=Unknown Channel}\",\n" +
                                    "    \"频道ID\": \"1398192763845214\"\n" +
                                    "  }\n" +
                                    "}")),
                    description = "消息发送失败，可能是由于Discord服务器问题或其他内部错误，请查看响应Body的“内容”子项以确定原因") } )
    @PostMapping("/discord")
    ResponseEntity<ResponseData> DiscordPush(
            @Parameter(description = "用于身份验证的API密钥", required = true, example = "ds1858dscc8745sfwe")
            @RequestHeader(value = "X-API-Key", required = true) String ApiKey,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "包含频道ID和消息内容的JSON对象",
                required = true,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PostMessageData.class) ) )
            @RequestBody PostMessageData RequestBody){
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
