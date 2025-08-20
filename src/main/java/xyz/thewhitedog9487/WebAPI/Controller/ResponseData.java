package xyz.thewhitedog9487.WebAPI.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "所有API通用的标准响应格式")
public record ResponseData(
        @Schema(description = "内部用响应代码" ,example = "201") int code,
        @Schema(description = "人类可读说明信息", example = "消息发送成功") Object message,
        @Schema(description = "具体的响应数据", example = "{\n" +
                "    \"新ID\": \"1407848258813825135\",\n" +
                "    \"内容\": \"写点什么好呢\",\n" +
                "    \"频道ID\": \"1398192763845214239\"\n" +
                "  }") Object data) {
    static ObjectMapper JsonMapper = new ObjectMapper();
    public ResponseData(int code) {
        this(code, null, null); }
    public ResponseData(int code, Object message) {
        this(code, List.of(message), null); }

    public String ToJson(){
        try {
            return JsonMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e); } }
}
