package xyz.thewhitedog9487.WebAPI.Controller

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonProperty
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.channel.TextChannel
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.withContext
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.*
import xyz.thewhitedog9487.WebAPI.Exception.DiscordBotTokenDoesNotConfiguredException
import xyz.thewhitedog9487.WebAPI.Exception.NoSuchDiscordChannelException
import xyz.thewhitedog9487.WebAPI.Miscellaneous.VirtualThreadCoroutineDispatcher
import xyz.thewhitedog9487.WebAPI.Miscellaneous.link
import java.net.URI

@Tag(name="远程消息处理相关")
@RestController
@RequestMapping("/message")
class Message(val DiscordBotClient: Kord?) {
    @JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
    @Schema(description = "包含了发送消息的数据包")
    data class PostMessageData(
        @Schema(description = "Discord频道ID", example = "1398192763845214239")
        @JsonProperty("ChannelId")
        val ChannelId: Long,
        @Schema(description = "要发送的消息内容", example = "成功完成备份，最新文件时间为2025 08 21")
        @JsonProperty("Content")
        val Content: String)

    @Operation(summary = "通过Discord Bot向指定频道发送消息", description =
"""这是一个私有API  
需要在请求头中提供X-API-Key以进行身份验证  
需要在请求体中提供频道ID和消息内容""")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "201",
            description = "消息发送成功，响应体中包含新消息的ID、频道ID和内容",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ResponseData::class),
                examples = [ExampleObject(value = """{
                      "code": 201,
                      "message": "消息发送成功",
                      "data": {
                        "新ID": "1407848258813825135",
                        "频道ID": "1398192763845214239",
                        "内容": "写点什么好呢"
                      }
                    }""") ] ) ] ),
        ApiResponse(
            responseCode = "400",
            description = "客户端发送的请求存在问题，请检查响应的data字段以获取更多信息",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ResponseData::class),
                examples = [ExampleObject(
                    name = "请求体无法解析",
                    value = """{
                          "code": 400,
                          "message": "请求体无法解析",
                          "data": {
                            "错误信息": "JSON parse error: Unexpected character ('}' (code 125)): was expecting double-quote to start field name"
                          }
                        }"""),
                    ExampleObject(
                        name = "频道不存在",
                        value =
                            """{
                          "code": 400,
                          "message": "消息发送失败",
                          "data": {
                            "错误信息": "ID为1398192763845214239的频道不存在",
                            "频道ID": "1398192763845214239"
                          }
                        }""") ] ) ] ),
        ApiResponse(
            responseCode = "401",
            description = "未提供API密钥",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ResponseData::class),
                examples = [ExampleObject(value = """{
                      "code": 401,
                      "message": "API密钥验证失败，未传递X-API-Key请求头",
                      "data": null
                    }""") ] ) ] ),
        ApiResponse(
            responseCode = "403",
            description = "API密钥无效",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ResponseData::class),
                examples = [ExampleObject(value = """{
                      "code": 403,
                      "message": "API密钥验证失败，API密钥无效",
                      "data": null
                    }""") ] ) ] ),
        ApiResponse(
            responseCode = "503",
            description = "服务端未配置Discord令牌，无法使用Discord相关功能",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ResponseData::class),
                examples = [ExampleObject(value = """{
                      "code": 503,
                      "message": "服务端尚未配置Discord令牌，无法使用Discord相关功能！",
                      "data": null
                    }""") ] ) ] ),
        ApiResponse(
            responseCode = "500",
            description =
                """
处理请求时发生未知错误，可能是由于Discord服务器问题或其他内部错误，请查看响应Body以确定原因  
另外，所有未被针对性处理的异常都会触发此响应
""",
            content = [Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = ResponseData::class),
                examples = [ExampleObject(value = """{
                      "code": 500,
                      "message": "处理请求时发生未知错误",
                      "data": {
                        "错误信息": "POST /channels/1398192763845214/messages returned 404 Not Found with response {code=10003, message=Unknown Channel}"
                      }
                    }""") ] ) ] ) ] )
    @PostMapping("/discord")
    suspend fun DiscordPush(
            @Parameter(description = "用于身份验证的API密钥", `in` = ParameterIn.HEADER, required = true, example = "ds1858dscc8745sfwe")
            @RequestHeader("X-API-Key") ApiKey: String,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "包含频道ID和消息内容的JSON对象",
                    required = true,
                    content = [Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = Schema(implementation = PostMessageData::class) ) ] )
            @RequestBody RequestBody: PostMessageData): ResponseEntity<ResponseData> {
        DiscordBotClient ?: throw DiscordBotTokenDoesNotConfiguredException()
        return withContext(VirtualThreadCoroutineDispatcher) {
            val ChannelID = Snowflake(RequestBody.ChannelId)
            val MessageData = DiscordBotClient!!
                .getChannelOf<TextChannel>(ChannelID)
                ?.createMessage(RequestBody.Content)
                ?: throw NoSuchDiscordChannelException(ChannelID)
            return@withContext ResponseEntity.created(URI.create(MessageData.link))
                .body(ResponseData(
                    HttpStatus.CREATED.value(),
                    "消息发送成功",
                    mapOf(
                        "新ID" to MessageData.id.toString(),
                        "频道ID" to ChannelID.value,
                        "内容" to RequestBody.Content ) ) ) } }

    @ExceptionHandler(DiscordBotTokenDoesNotConfiguredException::class)
    fun TriggerWhenDiscordBotTokenDoesNotConfigured(Exception: DiscordBotTokenDoesNotConfiguredException, Request: HttpServletRequest): ResponseEntity<ResponseData> {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(ResponseData(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                Exception.message ) ) }
    @ExceptionHandler(NoSuchDiscordChannelException::class)
    fun TriggerWhenSpecificChannelDoesNotExist(Exception: NoSuchDiscordChannelException, Request: HttpServletRequest): ResponseEntity<ResponseData> {
        return ResponseEntity.badRequest()
            .body(ResponseData(
                HttpStatus.BAD_REQUEST.value(),
                "消息发送失败",
                mapOf(
                    "错误信息" to Exception.message,
                    "频道ID" to Exception.TargetChannelId.value ) ) ) }
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun HandleMessageNotReadable(Exception: HttpMessageNotReadableException, Request: HttpServletRequest): ResponseEntity<ResponseData> {
        return ResponseEntity.badRequest()
            .body(ResponseData(
                HttpStatus.BAD_REQUEST.value(),
                "请求体无法解析",
                mapOf("错误信息" to Exception.message ) ) ) }
    @ExceptionHandler(Exception::class)
    fun HandleOtherException(Exception: Exception, Request: HttpServletRequest): ResponseEntity<ResponseData> {
        return ResponseEntity.internalServerError()
            .body(ResponseData(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "处理请求时发生未知错误",
                mapOf("错误信息" to Exception.message ) ) ) } }