package com.springcloud.ribbon.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by xu.guan on 2018/5/16.
 */
@RestController
public class HelloController {
    @Autowired
    private HelloService helloService;

    @RequestMapping("/hi")
    public String hi(@RequestParam String something){
        return "ribbo->"+helloService.hiService(something);
    }
}
