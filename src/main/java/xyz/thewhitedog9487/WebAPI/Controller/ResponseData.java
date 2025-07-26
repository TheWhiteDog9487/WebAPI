package xyz.thewhitedog9487.WebAPI.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;

import java.util.List;

public record ResponseData(int code, Object message, Object data) {
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
