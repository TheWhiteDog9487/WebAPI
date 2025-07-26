package xyz.thewhitedog9487.WebAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebApiApplication {

	public static void main(String[] args) {
		//TODO: 从配置文件或环境变量读取并设置HTTP端口号
		SpringApplication.run(WebApiApplication.class, args);
	}

}
