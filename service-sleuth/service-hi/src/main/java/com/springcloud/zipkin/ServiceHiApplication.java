package com.springcloud.zipkin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Logger;

@SpringBootApplication
@RestController
public class ServiceHiApplication {

	private Logger logger =Logger.getLogger(ServiceHiApplication.class.getName());

	public static void main(String[] args) {
		SpringApplication.run(ServiceHiApplication.class, args);
	}
	@Autowired
	private RestTemplate restTemplate;
	@Bean
	public RestTemplate getRestTemplate(){
		return new RestTemplate();
	}

	@RequestMapping("/hi")
	public String callHome(){
		logger.info("service-miya/miya is being called");
		return restTemplate.getForObject("http://localhost:8772/miya",String.class);
	}

	@RequestMapping("/home")
	public String home(){
		logger.info("calling trace service-hi home()");
		return "im service-hi";
	}

	/**
	 * 注入采样
	 * @return
	 */
	@Bean
	public AlwaysSampler defaultSampler(){
		return new AlwaysSampler();
	}
}
