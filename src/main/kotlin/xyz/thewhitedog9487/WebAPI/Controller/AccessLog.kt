package xyz.thewhitedog9487.WebAPI.Controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.Min
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import xyz.thewhitedog9487.WebAPI.Data.Entity.AccessLog
import xyz.thewhitedog9487.WebAPI.Data.Repository.AccessLogRepository
import xyz.thewhitedog9487.WebAPI.Data.Specification.AccessLogSpecification
import kotlin.reflect.full.declaredMemberProperties

@Tag(name = "日志相关")
@RestController
@RequestMapping("/accesslog")
class AccessLog(val AccessLogRepositoryInstance: AccessLogRepository) {

    @Operation(
        summary = "获取访问日志数据库内所有的记录",
        description = "这是一个私有API，需要在请求头中提供X-API-Key以进行身份验证")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "400",
                description = "请求参数错误",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ResponseData::class),
                        examples = [
                            ExampleObject(
                                name = "Limit小于0",
                                value = """{
                                  "code": 400,
                                  "message": "Limit参数不能小于0",
                                  "data": null
                                }""" ),
                            ExampleObject(
                                name = "字段不存在",
                                value = """{
                                  "code": 400,
                                  "message": "试图访问一个不存在的数据库字段",
                                  "data": "AccessLog类中不存在名为 NotExistField 的字段"
                                }""" ) ] ) ] ),
            ApiResponse(
                responseCode = "200",
                description = "成功获取到数据",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        array = ArraySchema(
                            minItems = 0,
                            schema = Schema(implementation = AccessLog::class) ),
                        examples = [
                            ExampleObject(
                                value = """[
                                  {
                                    "ID": 18,
                                    "RequestID": "3",
                                    "CF_Connecting_IP": "223.87.14.146",
                                    "X_Forwarded_For": "223.87.14.146, 141.101.99.156",
                                    "RemoteAddress": "192.168.128.11",
                                    "CF_IPCountry": "CN",
                                    "ISO3166": "CHN",
                                    "UserAgent": "Mozilla/5.0 (Linux; Android 10; SM-A202F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.101 Mobile Safari/537.36",
                                    "HttpMethod": "GET",
                                    "Protocol": "http",
                                    "ProtocolVersion": "HTTP/1.0",
                                    "URL": "http://dev.thewhitedog9487.xyz/",
                                    "QueryString": null,
                                    "Header": "host: dev.thewhitedog9487.xyz\nx-real-ip: 141.101.99.156\nx-forwarded-for: 223.87.14.146, 141.101.99.156\nx-forwarded-proto: https\nconnection: close\ncf-ray: 986883191fd5ed0b-LHR\nuser-agent: Mozilla/5.0 (Linux; Android 10; SM-A202F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.101 Mobile Safari/537.36\naccept-encoding: gzip, br\nreferer: https://www.baidu.com\ncdn-loop: cloudflare; loops=1\ncf-connecting-ip: 223.87.14.146\ncf-ipcountry: CN\ncf-visitor: {\"scheme\":\"https\"}",
                                    "Timestamp": "2025-09-29T03:49:42.766Z",
                                    "ResponseStatusCode": 302
                                  },
                                  {
                                    "ID": 261,
                                    "RequestID": "31",
                                    "CF_Connecting_IP": "2409:8962:77a:49c:a579:f5af:58a3:e2c",
                                    "X_Forwarded_For": "2409:8962:77a:49c:a579:f5af:58a3:e2c, 172.71.150.157",
                                    "RemoteAddress": "192.168.128.11",
                                    "CF_IPCountry": "CN",
                                    "ISO3166": "CHN",
                                    "UserAgent": "IntelliJ HTTP Client/IntelliJ IDEA 2025.2.3",
                                    "HttpMethod": "GET",
                                    "Protocol": "http",
                                    "ProtocolVersion": "HTTP/1.0",
                                    "URL": "http://dev.thewhitedog9487.xyz/accesslog",
                                    "QueryString": null,
                                    "Header": "host: dev.thewhitedog9487.xyz\nx-real-ip: 172.71.150.157\nx-forwarded-for: 2409:8962:77a:49c:a579:f5af:58a3:e2c, 172.71.150.157\nx-forwarded-proto: https\nconnection: close\nx-api-key: asdasdaFSEA452453sdcv\nuser-agent: IntelliJ HTTP Client/IntelliJ IDEA 2025.2.3\naccept: */*\ncf-ray: 98cc7d6aaea1c37a-SEA\naccept-encoding: gzip, br\ncdn-loop: cloudflare; loops=1\ncf-connecting-ip: 2409:8962:77a:49c:a579:f5af:58a3:e2c\ncf-ipcountry: CN\ncf-visitor: {\"scheme\":\"https\"}\ncookie: JSESSIONID=A49527B913170D86A5544A252BD31040",
                                    "Timestamp": "2025-10-11T07:02:05.718Z",
                                    "ResponseStatusCode": 200
                                  }
                                ]""" ) ] ) ] ),
            ApiResponse(
                responseCode = "401",
                description = "未提供API密钥",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ResponseData::class),
                        examples = [
                            ExampleObject(
                                value = """{
                                  "code": 401,
                                  "message": "API密钥验证失败，未传递X-API-Key请求头",
                                  "data": null
                                }""" ) ] ) ] ),
            ApiResponse(
                responseCode = "403",
                description = "API密钥无效",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ResponseData::class),
                        examples = [
                            ExampleObject(
                                value = """{
                                  "code": 403,
                                  "message": "API密钥验证失败，API密钥无效",
                                  "data": null
                                }""" ) ] ) ] ) ] )
    @GetMapping
    fun GetLog(@Parameter(description = "用于身份验证的API密钥", `in` = ParameterIn.HEADER, required = true, example = "ds1858dscc8745sfwe" )
               @RequestHeader("X-API-Key") ApiKey: String,

               @Min(0)
               @Parameter(description = "返回记录的数量限制，0表示不限制", example = "10")
               @RequestParam(defaultValue = "0") Limit: Int,

               @Parameter(description = "排序方向：ASC 升序 从小到大 / DESC 降序 从大到小", example = "DESC")
               @RequestParam(defaultValue = "ASC") Direction: Sort.Direction,

               @Parameter(description = "排序字段名", example = "ID")
               @RequestParam(defaultValue = "Timestamp") OrderByFieldName: String): List<AccessLog> {
        Limit < 0 && throw IllegalArgumentException("Limit参数不能小于0")
        AccessLog::class.declaredMemberProperties.all{
            it.name != OrderByFieldName } && throw NoSuchFieldException("${AccessLog::class.simpleName}类中不存在名为 $OrderByFieldName 的字段")

        val Results = AccessLogRepositoryInstance.findAll(AccessLogSpecification.OrderByField(OrderByFieldName, Direction))
        // ↑ ↓ 不能用 AccessLogRepositoryInstance.findAll(Sort.by()) ，字段名处理有问题，传入排序字段Abc会在不知道什么地方变成abc导致查询失败
        return if (Limit > 0) Results.take(Limit) else Results }

    @ExceptionHandler(IllegalArgumentException::class)
    fun TriggerWhenLimitLessThanZero(e: IllegalArgumentException): ResponseEntity<ResponseData> {
        return ResponseEntity.badRequest()
            .body(ResponseData(HttpStatus.BAD_REQUEST.value(), message = e.message ) ) }

    @ExceptionHandler(NoSuchFieldException::class)
    fun TriggerWhenAccessFieldThatNotExist(e: NoSuchFieldException): ResponseEntity<ResponseData> {
        return ResponseEntity.badRequest()
            .body(ResponseData(HttpStatus.BAD_REQUEST.value(), "试图访问一个不存在的数据库字段", e.message ) ) } }