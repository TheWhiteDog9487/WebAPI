package xyz.thewhitedog9487.WebAPI.Controller

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import tools.jackson.databind.ObjectMapper

@Schema(description = "所有API通用的标准响应格式")
data class ResponseData(
    @Schema(description = "内部用响应代码", example = "201") val code: Int,
    @Schema(description = "人类可读说明信息", example = "消息发送成功") val message: Any? = null,
    @Schema(description = "具体的响应数据", example = """
        {
            "新ID": "1407848258813825135",
            "内容": "写点什么好呢",
            "频道ID": "1398192763845214239"
        }
    """) val data: Any? = null){
    constructor(code: Int, message: Any): this(code, message, null)

    @JsonIgnore
    @Schema(hidden = true)
    val json: String = JsonMapper.writeValueAsString(this)

    companion object{
        val JsonMapper = ObjectMapper() } }