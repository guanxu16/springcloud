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
public class ServiceMiyaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceMiyaApplication.class, args);
	}

	private Logger logger = Logger.getLogger(ServiceMiyaApplication.class.getName());

	@Autowired
	private RestTemplate restTemplate;

	@Bean
	public RestTemplate getRestTemplate(){
		return new RestTemplate();
	}

	@RequestMapping("/hi")
	public String home(){
		logger.info("hi is being called");
		return "hi,i'm miya!";
	}
	@RequestMapping("/miya")
	public String info(){
		logger.info("service-hi/home is bing called");
		return restTemplate.getForObject("http://localhost:8771/home",String.class);
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
