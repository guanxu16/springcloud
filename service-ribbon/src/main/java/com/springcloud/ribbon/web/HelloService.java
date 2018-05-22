package com.springcloud.ribbon.web;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by xu.guan on 2018/5/16.
 */
@Service
public class HelloService {
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 通过断路器当服务不可用时调用断路器方法
     * @param something
     * @return
     */
    @HystrixCommand(fallbackMethod = "hiError")
    public String hiService(String something){
        return restTemplate.getForObject("http://SERVICE-SAYHI/say?something="+something,String.class);
    }

    /**
     * 当服务不可用是调用此方法
     * @param something
     * @return
     */
    public String hiError(String something){
        return "hi,"+something+",its a error";
    }

}
