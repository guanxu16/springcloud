package com.springcloud.eureka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@EnableEurekaClient
@SpringBootApplication
@RestController
public class EurekaClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaClientApplication.class, args);
	}

	@Value("${server.port}")
	String port;
	@RequestMapping("/say")
	public String say(@RequestParam String something){
		return "port:"+port+",say:"+something;
	}
}
